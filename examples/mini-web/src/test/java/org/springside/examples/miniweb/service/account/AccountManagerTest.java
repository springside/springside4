package org.springside.examples.miniweb.service.account;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springside.examples.miniweb.dao.account.UserDao;
import org.springside.examples.miniweb.service.ServiceException;
import org.springside.modules.test.security.shiro.ShiroTestUtils;

/**
 * SecurityEntityManager的测试用例, 测试Service层的业务逻辑.
 * 
 * @author calvin
 */
public class AccountManagerTest {

	private AccountManager accountManager;
	@Mock
	private UserDao mockUserDao;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		ShiroTestUtils.mockSubject("foo");

		accountManager = new AccountManager();
		accountManager.setUserDao(mockUserDao);
	}

	@After
	public void tearDown() {
		ShiroTestUtils.clearSubject();
	}

	@Test
	public void deleteUser() {
		//正常删除用户.
		accountManager.deleteUser(2L);

		//删除超级管理用户抛出异常.
		try {
			accountManager.deleteUser(1L);
			fail("expected ServicExcepton not be thrown");
		} catch (ServiceException e) {
			//expected exception
		}
	}
}
