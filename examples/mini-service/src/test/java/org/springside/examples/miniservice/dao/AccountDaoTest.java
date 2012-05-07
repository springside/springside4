package org.springside.examples.miniservice.dao;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springside.examples.miniservice.data.AccountData;
import org.springside.examples.miniservice.entity.Department;
import org.springside.examples.miniservice.entity.User;
import org.springside.modules.test.spring.SpringTransactionalTestCase;

import com.google.common.collect.Maps;

/**
 * AccountDao的集成测试用例,测试ORM映射及SQL操作.
 * 
 * 默认在每个测试函数后进行回滚.
 * 
 * @author calvin
 */
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class AccountDaoTest extends SpringTransactionalTestCase {

	@Autowired
	private AccountDao accountDao;

	@Test
	public void getDepartmentDetail() {
		Department department = accountDao.getDepartmentDetail(1L);
		assertEquals("Development", department.getName());
		assertEquals("Jack", department.getManager().getName());
		assertEquals(2, department.getUserList().size());
		assertEquals("Jack", department.getUserList().get(0).getName());
	}

	@Test
	public void getUser() {
		User user = accountDao.getUser(1L);
		assertEquals("user1", user.getLoginName());
		assertEquals(new Long(1L), user.getDepartment().getId());

		user = accountDao.getUser(999L);
		assertNull(user);
	}

	@Test
	public void searchUser() {
		Map<String, Object> parameters = Maps.newHashMap();

		parameters.put("loginName", null);
		parameters.put("name", null);
		List<User> result = accountDao.searchUser(parameters);
		assertEquals(4, result.size());

		parameters.put("loginName", "user1");
		parameters.put("name", null);
		result = accountDao.searchUser(parameters);
		assertEquals(1, result.size());

		parameters.clear();
		parameters.put("name", "Jack");
		parameters.put("loginName", null);
		result = accountDao.searchUser(parameters);
		assertEquals(1, result.size());

		parameters.clear();
		parameters.put("name", "Jack");
		parameters.put("loginName", "user1");
		result = accountDao.searchUser(parameters);
		assertEquals(1, result.size());

		parameters.clear();
		parameters.put("name", "Jack");
		parameters.put("loginName", "errorName");
		result = accountDao.searchUser(parameters);
		assertEquals(0, result.size());
	}

	@Test
	public void saveUser() {
		User user = AccountData.getRandomUser();
		Long id = accountDao.saveUser(user);
		assertEquals(new Long(5L), id);

		assertEquals(4 + 1, this.countRowsInTable("acct_user"));
		assertEquals(2 + 1, accountDao.getDepartmentDetail(1L).getUserList().size());
	}

}
