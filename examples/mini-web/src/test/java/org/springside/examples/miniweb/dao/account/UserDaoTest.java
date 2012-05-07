package org.springside.examples.miniweb.dao.account;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springside.examples.miniweb.data.AccountData;
import org.springside.examples.miniweb.entity.account.User;
import org.springside.modules.test.spring.SpringTransactionalTestCase;

/**
 * UserDao的测试用例, 测试ORM映射及特殊的DAO操作.
 * 
 * 默认在每个测试函数后进行回滚.
 * 
 * @author calvin
 */
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class UserDaoTest extends SpringTransactionalTestCase {

	@Autowired
	private UserDao entityDao;
	@PersistenceContext
	private EntityManager em;

	@Test
	//如果你需要真正插入数据库,将Rollback设为false
	//@Rollback(false) 
	public void crudEntityWithGroup() {
		//新建并保存带权限组的用户
		User user = AccountData.getRandomUserWithOneGroup();
		entityDao.save(user);
		em.flush();

		//获取用户
		user = entityDao.findOne(user.getId());
		assertEquals(1, user.getGroupList().size());

		//删除用户的权限组
		user.getGroupList().remove(0);
		entityDao.save(user);
		em.flush();

		user = entityDao.findOne(user.getId());
		assertEquals(0, user.getGroupList().size());

		//删除用户
		entityDao.delete(user.getId());
		em.flush();

		user = entityDao.findOne(user.getId());
		assertNull(user);
	}

	//期望抛出ConstraintViolationException的异常.
	@Test(expected = org.springframework.dao.DataIntegrityViolationException.class)
	public void savenUserNotUnique() {
		User user = AccountData.getRandomUser();
		user.setLoginName("admin");
		entityDao.save(user);
		em.flush();
	}
}