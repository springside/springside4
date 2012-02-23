package org.springside.examples.showcase.common.service;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springside.examples.showcase.cache.memcached.MemcachedObjectType;
import org.springside.examples.showcase.common.dao.UserHibernateDao;
import org.springside.examples.showcase.common.dao.UserMyBatisDao;
import org.springside.examples.showcase.common.entity.User;
import org.springside.examples.showcase.jms.simple.NotifyMessageProducer;
import org.springside.examples.showcase.security.ShiroDbRealm;
import org.springside.modules.mapper.JsonMapper;
import org.springside.modules.memcached.SpyMemcachedClient;
import org.springside.modules.utils.security.Digests;

/**
 * 用户管理类.
 * 
 * @author calvin
 */
//Spring Service Bean的标识.
@Component
public class AccountManager {
	private static Logger logger = LoggerFactory.getLogger(AccountManager.class);

	private UserHibernateDao userHibernateDao;

	private UserMyBatisDao userMyBatisDao;

	private SpyMemcachedClient memcachedClient;

	private JsonMapper jsonBinder = JsonMapper.buildNonDefaultMapper();

	private NotifyMessageProducer notifyProducer; //JMS消息发送

	private ShiroDbRealm shiroRealm;

	/**
	 * 在保存用户时,发送用户修改通知消息, 由消息接收者异步进行较为耗时的通知邮件发送.
	 * 
	 * 如果企图修改超级用户,取出当前操作员用户,打印其信息然后抛出异常.
	 * 
	 */
	//演示指定非默认名称的TransactionManager.
	@Transactional("defaultTransactionManager")
	public void saveUser(User user) {

		if (isSupervisor(user)) {
			logger.warn("操作员{}尝试修改超级管理员用户", SecurityUtils.getSubject().getPrincipal());
			throw new ServiceException("不能修改超级管理员用户");
		}

		String shaPassword = Digests.sha1Hex(user.getPlainPassword());
		user.setShaPassword(shaPassword);

		userHibernateDao.save(user);

		if (shiroRealm != null) {
			shiroRealm.clearCachedAuthorizationInfo(user.getLoginName());
		}

		sendNotifyMessage(user);
	}

	/**
	 * 判断是否超级管理员.
	 */
	private boolean isSupervisor(User user) {
		return (user.getId() != null && user.getId() == 1L);
	}

	@Transactional(readOnly = true)
	public User getUser(Long id) {
		return userHibernateDao.get(id);
	}

	/**
	 * 取得用户, 并对用户的延迟加载关联进行初始化.
	 */
	@Transactional(readOnly = true)
	public User getInitializedUser(Long id) {
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
			//因为hibernate的proxy问题多多,此处使用jdbc
			user = userMyBatisDao.getUser(id);
			if (user != null) {
				jsonString = jsonBinder.toJson(user);
				memcachedClient.set(key, MemcachedObjectType.USER.getExpiredTime(), jsonString);
			}
		} else {
			user = jsonBinder.fromJson(jsonString, User.class);
		}
		return user;
	}

	/**
	 * 按名称查询用户, 并对用户的延迟加载关联进行初始化.
	 */
	@Transactional(readOnly = true)
	public User searchInitializedUserByName(String name) {
		User user = userHibernateDao.findUniqueBy("name", name);
		userHibernateDao.initUser(user);
		return user;
	}

	/**
	 * 取得所有用户, 预加载用户的角色.
	 */
	@Transactional(readOnly = true)
	public List<User> getAllUserWithRole() {
		List<User> list = userHibernateDao.getAllUserWithRoleByDistinctHql();
		logger.info("get {} user sucessful.", list.size());
		return list;
	}

	/**
	 * 获取当前用户数量.
	 */
	@Transactional(readOnly = true)
	public Long getUserCount() {
		return userHibernateDao.getUserCount();
	}

	@Transactional(readOnly = true)
	public User findUserByLoginName(String loginName) {
		return userHibernateDao.findUniqueBy("loginName", loginName);
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
	public void setUserHibernateDao(UserHibernateDao userDao) {
		this.userHibernateDao = userDao;
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
}
