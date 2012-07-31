package org.springside.examples.showcase.repository.mybatis;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springside.examples.showcase.data.UserData;
import org.springside.examples.showcase.entity.User;
import org.springside.modules.test.spring.SpringTransactionalTestCase;

import com.google.common.collect.Maps;

@DirtiesContext
@ContextConfiguration(locations = { "/applicationContext.xml" })
@TransactionConfiguration(transactionManager = "defaultTransactionManager")
public class UserMybatisDaoTest extends SpringTransactionalTestCase {

	@Autowired
	private UserMybatisDao userDao;

	@Test
	public void getUser() throws Exception {
		User user = userDao.getUser(1L);
		assertEquals("admin", user.getLoginName());
	}

	@Test
	public void searchUser() throws Exception {
		Map<String, Object> parameter = Maps.newHashMap();
		parameter.put("name", "Admin");
		List<User> result = userDao.searchUser(parameter);
		assertEquals(1, result.size());
		assertEquals("admin", result.get(0).getLoginName());
	}

	@Test
	public void createAndDeleteUser() throws Exception {
		//create
		int count = countRowsInTable("ss_user");
		User user = UserData.randomUser();
		userDao.saveUser(user);
		Long id = user.getId();

		assertEquals(count + 1, countRowsInTable("ss_user"));
		User result = userDao.getUser(id);
		assertEquals(user.getLoginName(), result.getLoginName());

		//delete
		userDao.deleteUser(id);
		assertEquals(count, countRowsInTable("ss_user"));
		assertNull(userDao.getUser(id));
	}

}
