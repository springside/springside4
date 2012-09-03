package org.springside.examples.quickstart.service.account;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springside.examples.quickstart.repository.UserDao;
import org.springside.examples.quickstart.service.ServiceException;
import org.springside.examples.quickstart.service.account.ShiroDbRealm.ShiroUser;
import org.springside.modules.test.security.shiro.ShiroTestUtils;

/**
 * AccountService的测试用例, 测试Service层的业务逻辑.
 * 
 * @author calvin
 */
public class AccountServiceTest {

	private AccountService accountService;
	@Mock
	private UserDao mockUserDao;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		accountService = new AccountService();
		accountService.setUserDao(mockUserDao);

		ShiroTestUtils.mockSubject(new ShiroUser(3L, "foo", "Foo"));
	}

	@Test
	public void deleteUser() {
		//正常删除用户.
		accountService.deleteUser(2L);
		Mockito.verify(mockUserDao).delete(2L);

		//删除超级管理用户抛出异常.
		try {
			accountService.deleteUser(1L);
			fail("expected ServicExcepton not be thrown");
		} catch (ServiceException e) {
			//expected exception
		}
		Mockito.verify(mockUserDao, Mockito.never()).delete(1L);
	}
}
