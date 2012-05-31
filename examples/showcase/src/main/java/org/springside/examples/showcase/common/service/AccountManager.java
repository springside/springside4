package org.springside.examples.showcase.common.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springside.examples.showcase.cache.memcached.MemcachedObjectType;
import org.springside.examples.showcase.common.dao.UserJpaDao;
import org.springside.examples.showcase.common.dao.UserMyBatisDao;
import org.springside.examples.showcase.common.entity.User;
import org.springside.examples.showcase.jms.simple.NotifyMessageProducer;
import org.springside.examples.showcase.jmx.ApplicationStatistics;
import org.springside.examples.showcase.security.ShiroDbRealm;
import org.springside.examples.showcase.security.ShiroDbRealm.HashPassword;
import org.springside.modules.cache.memcached.SpyMemcachedClient;
import org.springside.modules.mapper.JsonMapper;
import org.springside.modules.orm.Hibernates;

/**
 * 用户管理类.
 * 
 * @author calvin
 */
//Spring Service Bean的标识.
@Component
@Transactional(readOnly = true)
public class AccountManager {
	private static Logger logger = LoggerFactory.getLogger(AccountManager.class);

	private UserJpaDao userJpaDao;

	private UserMyBatisDao userMyBatisDao;

	private SpyMemcachedClient memcachedClient;

	private JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();

	private NotifyMessageProducer notifyProducer; //JMS消息发送

	private ShiroDbRealm shiroRealm;

	private ApplicationStatistics applicationStatistics;

	/**
	 * 在保存用户时,发送用户修改通知消息, 由消息接收者异步进行较为耗时的通知邮件发送.
	 * 
	 * 如果企图修改超级用户,取出当前操作员用户,打印其信息然后抛出异常.
	 * 
	 */
	@Transactional(readOnly = false)
	public void saveUser(User user) {

		if (isSupervisor(user)) {
			logger.warn("操作员{}尝试修改超级管理员用户", SecurityUtils.getSubject().getPrincipal());
			throw new ServiceException("不能修改超级管理员用户");
		}

		//设定安全的密码，使用passwordService提供的salt并经过1024次 sha-1 hash
		if (StringUtils.isNotBlank(user.getPlainPassword()) && shiroRealm != null) {
			HashPassword hashPassword = shiroRealm.encrypt(user.getPlainPassword());
			user.setSalt(hashPassword.salt);
			user.setPassword(hashPassword.password);
		}

		userJpaDao.save(user);

		if (shiroRealm != null) {
			shiroRealm.clearCachedAuthorizationInfo(user.getLoginName());
		}

		if (applicationStatistics != null) {
			applicationStatistics.incrUpdateUserTimes();
		}

		sendNotifyMessage(user);

	}

	public List<User> getAllUser() {

		if (applicationStatistics != null) {
			applicationStatistics.incrListUserTimes();
		}
		return (List<User>) userJpaDao.findAll();
	}

	public List<User> getAllUserInitialized() {
		List<User> result = (List<User>) userJpaDao.findAll();
		for (User user : result) {
			Hibernates.initLazyProperty(user.getRoleList());
		}
		return result;
	}

	/**
	 * 判断是否超级管理员.
	 */
	private boolean isSupervisor(User user) {
		return (user.getId() != null && user.getId() == 1L);
	}

	public User getUser(Long id) {
		return userJpaDao.findOne(id);
	}

	/**
	 * 取得用户，先尝试从缓存获取，然后用Mybatis查询.
	 */
	public User getUserEffective(Long id) {
		if (memcachedClient != null) {
			return getUserFromMemcached(id);
		} else {
			return userMyBatisDao.getUser(id);
		}
	}

	/**
	 * 访问Memcached, 使用JSON字符串存放对象以节约空间.
	 */
	private User getUserFromMemcached(Long id) {

		String key = MemcachedObjectType.USER.getPrefix() + id;

		User user = null;
		String jsonString = memcachedClient.get(key);

		if (jsonString == null) {
			//用户不在 memcached中,从数据库中取出并放入memcached.
			//因为hibernate的proxy在序列化时问题多多,此处使用jdbc
			user = userMyBatisDao.getUser(id);
			if (user != null) {
				jsonString = jsonMapper.toJson(user);
				memcachedClient.set(key, MemcachedObjectType.USER.getExpiredTime(), jsonString);
			}
		} else {
			user = jsonMapper.fromJson(jsonString, User.class);
		}
		return user;
	}

	/**
	 * 按名称查询用户, 并对用户的延迟加载关联进行初始化.
	 */
	public User findUserByNameInitialized(String name) {
		User user = userJpaDao.findByName(name);
		if (user != null) {
			Hibernates.initLazyProperty(user.getRoleList());
		}
		return user;
	}

	/**
	 * 获取当前用户数量.
	 */
	public Long getUserCount() {
		return userJpaDao.count();
	}

	public User findUserByLoginName(String loginName) {
		return userJpaDao.findByLoginName(loginName);
	}

	/**
	 * 发送用户变更消息.
	 * 
	 * 同时发送只有一个消费者的Queue消息与发布订阅模式有多个消费者的Topic消息.
	 */
	private void sendNotifyMessage(User user) {
		if (notifyProducer != null) {
			try {
				notifyProducer.sendQueue(user);
				notifyProducer.sendTopic(user);
			} catch (Exception e) {
				logger.error("消息发送失败", e);
			}
		}
	}

	@Autowired
	public void setUserJpaDao(UserJpaDao userJpaDao) {
		this.userJpaDao = userJpaDao;
	}

	@Autowired
	public void setUserMyBatisDao(UserMyBatisDao userMyBatisDao) {
		this.userMyBatisDao = userMyBatisDao;
	}

	@Autowired(required = false)
	public void setNotifyProducer(NotifyMessageProducer notifyProducer) {
		this.notifyProducer = notifyProducer;
	}

	@Autowired(required = false)
	public void setMemcachedClient(SpyMemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}

	@Autowired(required = false)
	public void setShiroRealm(ShiroDbRealm shiroRealm) {
		this.shiroRealm = shiroRealm;
	}

	@Autowired(required = false)
	public void setApplicationStatistics(ApplicationStatistics applicationStatistics) {
		this.applicationStatistics = applicationStatistics;
	}
}
