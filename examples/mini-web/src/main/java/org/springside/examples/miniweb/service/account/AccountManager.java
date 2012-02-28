package org.springside.examples.miniweb.service.account;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springside.examples.miniweb.dao.account.GroupDao;
import org.springside.examples.miniweb.dao.account.UserDao;
import org.springside.examples.miniweb.entity.account.Group;
import org.springside.examples.miniweb.entity.account.User;
import org.springside.examples.miniweb.service.ServiceException;

/**
 * 安全相关实体的管理类,包括用户和权限组.
 * 
 * @author calvin
 */
//Spring Bean的标识.
@Component
//默认将类中的所有public函数纳入事务管理.
@Transactional
public class AccountManager {

	private static Logger logger = LoggerFactory.getLogger(AccountManager.class);

	private UserDao userDao;
	private GroupDao groupDao;
	private ShiroDbRealm shiroRealm;

	//-- User Manager --//
	@Transactional(readOnly = true)
	public User getUser(Long id) {
		return userDao.findOne(id);
	}

	public void saveUser(User entity) {
		userDao.save(entity);
		shiroRealm.clearCachedAuthorizationInfo(entity.getLoginName());
	}

	/**
	 * 删除用户,如果尝试删除超级管理员将抛出异常.
	 */
	public void deleteUser(Long id) {
		if (isSupervisor(id)) {
			logger.warn("操作员{}尝试删除超级管理员用户", SecurityUtils.getSubject().getPrincipal());
			throw new ServiceException("不能删除超级管理员用户");
		}
		userDao.delete(id);
	}

	/**
	 * 判断是否超级管理员.
	 */
	private boolean isSupervisor(Long id) {
		return id == 1;
	}

	@Transactional(readOnly = true)
	public List<User> getAllUser() {
		return (List<User>) userDao.findAll(new Sort(Direction.ASC, "id"));
	}

	@Transactional(readOnly = true)
	public User findUserByLoginName(String loginName) {
		return userDao.findByLoginName(loginName);
	}

	/**
	 * 检查用户名是否唯一.
	 *
	 * @return newLoginName在数据库中唯一或等于oldLoginName时返回true.
	 */
	@Transactional(readOnly = true)
	public boolean isLoginNameUnique(String newLoginName, String oldLoginName) {
		return true;
		//TODO
		//return userDao.isPropertyUnique("loginName", newLoginName, oldLoginName);
	}

	//-- Group Manager --//
	@Transactional(readOnly = true)
	public Group getGroup(Long id) {
		return groupDao.findOne(id);
	}

	@Transactional(readOnly = true)
	public List<Group> getAllGroup() {
		return (List<Group>) groupDao.findAll((new Sort(Direction.ASC, "id")));
	}

	public void saveGroup(Group entity) {
		groupDao.save(entity);
		shiroRealm.clearAllCachedAuthorizationInfo();
	}

	public void deleteGroup(Long id) {
		groupDao.delete(id);
		shiroRealm.clearAllCachedAuthorizationInfo();
	}

	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	@Autowired
	public void setGroupDao(GroupDao groupDao) {
		this.groupDao = groupDao;
	}

	@Autowired(required = false)
	public void setShiroRealm(ShiroDbRealm shiroRealm) {
		this.shiroRealm = shiroRealm;
	}
}
