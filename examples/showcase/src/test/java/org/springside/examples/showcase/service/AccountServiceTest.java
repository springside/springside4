/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springside.examples.showcase.entity.User;
import org.springside.examples.showcase.repository.jpa.UserDao;
import org.springside.examples.showcase.service.ShiroDbRealm.ShiroUser;
import org.springside.modules.test.security.shiro.ShiroTestUtils;

public class AccountServiceTest {

	@InjectMocks
	private AccountService accountService;

	@Mock
	private UserDao mockUserDao;

	@Mock
	private BusinessLogger businessLogger;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		ShiroTestUtils.mockSubject(new ShiroUser("foo", "Foo"));
	}

	@After
	public void tearDown() {
		ShiroTestUtils.clearSubject();
	}

	@Test
	public void saveUser() {
		User admin = new User();
		admin.setId(1L);

		User user = new User();
		user.setId(2L);
		user.setPlainPassword("123");

		// 正常保存用户.
		accountService.saveUser(user);

		// 保存超级管理用户抛出异常.
		try {
			accountService.saveUser(admin);
			failBecauseExceptionWasNotThrown(ServiceException.class);
		} catch (ServiceException e) {
			// expected exception
		}
		Mockito.verify(mockUserDao, Mockito.never()).delete(1L);
	}
}
