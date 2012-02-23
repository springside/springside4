package org.springside.examples.showcase.common;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springside.examples.showcase.common.dao.UserHibernateDao;
import org.springside.examples.showcase.common.entity.User;
import org.springside.examples.showcase.common.service.AccountManager;
import org.springside.examples.showcase.common.service.ServiceException;
import org.springside.modules.test.utils.ShiroTestHelper;

public class AccountManagerTest {

	private AccountManager accountManager;
	@Mock
	private UserHibernateDao mockUserDao;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		ShiroTestHelper.mockSubject("foo");

		accountManager = new AccountManager();
		accountManager.setUserHibernateDao(mockUserDao);
	}

	@After
	public void tearDown() {
		ShiroTestHelper.clearSubject();
	}

	@Test
	public void saveUser() {
		User admin = new User();
		admin.setId(1L);

		User user = new User();
		user.setId(2L);
		user.setPlainPassword("123");

		//正常保存用户.
		accountManager.saveUser(user);

		//保存超级管理用户抛出异常.
		try {
			accountManager.saveUser(admin);
			fail("expected ServicExcepton not be thrown");
		} catch (ServiceException e) {
			//expected exception
		}
	}
}
