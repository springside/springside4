package org.springside.examples.showcase.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springside.examples.showcase.demos.jms.simple.NotifyMessageProducer;
import org.springside.examples.showcase.demos.jmx.ApplicationStatistics;
import org.springside.examples.showcase.entity.Role;
import org.springside.examples.showcase.entity.User;
import org.springside.examples.showcase.repository.jpa.RoleDao;
import org.springside.examples.showcase.repository.jpa.UserDao;
import org.springside.examples.showcase.service.ShiroDbRealm.ShiroUser;
import org.springside.modules.persistence.DynamicSpecifications;
import org.springside.modules.persistence.Hibernates;
import org.springside.modules.persistence.SearchFilter;
import org.springside.modules.security.utils.Digests;
import org.springside.modules.utils.Encodes;

import com.google.common.collect.Maps;

/**
 * 用户管理业务类.
 * 
 * @author calvin
 */
// Spring Service Bean的标识.
@Component
@Transactional
public class AccountService {
	public static final String HASH_ALGORITHM = "SHA-1";
	public static final int HASH_INTERATIONS = 1024;
	private static final int SALT_SIZE = 8;

	private static Logger logger = LoggerFactory.getLogger(AccountService.class);
	private UserDao userDao;

	private RoleDao roleDao;

	private NotifyMessageProducer notifyProducer; // JMS消息发送

	private ApplicationStatistics applicationStatistics;

	private BusinessLogger businessLogger;

	/**
	 * 在保存用户时,发送用户修改通知消息, 由消息接收者异步进行较为耗时的通知邮件发送.
	 * 
	 * 如果企图修改超级用户,取出当前操作员用户,打印其信息然后抛出异常.
	 * 
	 */
	public void saveUser(User user) {

		if (isSupervisor(user)) {
			logger.warn("操作员{}尝试修改超级管理员用户", getCurrentUserName());
			throw new ServiceException("不能修改超级管理员用户");
		}

		// 设定安全的密码，生成随机的salt并经过1024次 sha-1 hash
		if (StringUtils.isNotBlank(user.getPlainPassword())) {
			entryptPassword(user);
		}

		userDao.save(user);

		// 发送JMS消息
		sendNotifyMessage(user);

		// 运行统计演示
		if (applicationStatistics != null) {
			applicationStatistics.incrUpdateUserTimes();
		}

		// 业务日志演示
		if (businessLogger != null) {
			Map map = Maps.newHashMap();
			map.put("userId", user.getId());
			businessLogger.log("UPDATE", getCurrentUserName(), map);
		}
	}

	/**
	 * 设定安全的密码，生成随机的salt并经过1024次 sha-1 hash
	 */
	private void entryptPassword(User user) {
		byte[] salt = Digests.generateSalt(SALT_SIZE);
		user.setSalt(Encodes.encodeHex(salt));

		byte[] hashPassword = Digests.sha1(user.getPlainPassword().getBytes(), salt, HASH_INTERATIONS);
		user.setPassword(Encodes.encodeHex(hashPassword));
	}

	public List<User> searchUser(Map<String, Object> searchParams) {
		Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
		Specification<User> spec = DynamicSpecifications.bySearchFilter(filters.values(), User.class);
		List<User> userList = userDao.findAll(spec);

		// 运行统计演示
		if (applicationStatistics != null) {
			applicationStatistics.incrListUserTimes();
		}
		// 业务日志演示
		if (businessLogger != null) {
			businessLogger.log("LIST", getCurrentUserName(), null);
		}
		return userList;
	}

	/**
	 * 获取全部用户对象，并在返回前完成LazyLoad属性的初始化。
	 */
	public List<User> getAllUserInitialized() {
		List<User> result = (List<User>) userDao.findAll();
		for (User user : result) {
			Hibernates.initLazyProperty(user.getRoleList());
		}
		return result;
	}

	/**
	 * 判断是否超级管理员.
	 */
	private boolean isSupervisor(User user) {
		return ((user.getId() != null) && (user.getId() == 1L));
	}

	public User getUser(Long id) {
		return userDao.findOne(id);
	}

	/**
	 * 按名称查询用户, 并对用户的延迟加载关联进行初始化.
	 */
	public User findUserByNameInitialized(String name) {
		User user = userDao.findByName(name);
		if (user != null) {
			Hibernates.initLazyProperty(user.getRoleList());
		}
		return user;
	}

	/**
	 * 获取当前用户数量.
	 */
	public Long getUserCount() {
		return userDao.count();
	}

	public User findUserByLoginName(String loginName) {
		return userDao.findByLoginName(loginName);
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

	/**
	 * 取出Shiro中的当前用户LoginName.
	 */
	private String getCurrentUserName() {
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		return user.loginName;
	}

	// --------------------//
	// Role Management //
	// --------------------//

	public List<Role> getAllRole() {
		return (List<Role>) roleDao.findAll();
	}

	// -----------------//
	// Setter methods //
	// -----------------//

	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	@Autowired
	public void setRoleDao(RoleDao roleDao) {
		this.roleDao = roleDao;
	}

	@Autowired(required = false)
	public void setNotifyProducer(NotifyMessageProducer notifyProducer) {
		this.notifyProducer = notifyProducer;
	}

	@Autowired(required = false)
	public void setApplicationStatistics(ApplicationStatistics applicationStatistics) {
		this.applicationStatistics = applicationStatistics;
	}

	@Autowired(required = false)
	public void setBusinessLogger(BusinessLogger businessLogger) {
		this.businessLogger = businessLogger;
	}
}
