/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.repository.mybatis;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springside.examples.showcase.data.UserData;
import org.springside.examples.showcase.entity.User;
import org.springside.modules.test.spring.SpringTransactionalTestCase;

import com.google.common.collect.Maps;

@DirtiesContext
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class UserMybatisDaoTest extends SpringTransactionalTestCase {

	@Autowired
	private UserMybatisDao userDao;

	@Test
	public void getUser() throws Exception {
		User user = userDao.get(1L);
		assertThat(user).as("User not found").isNotNull();
		assertThat(user.getLoginName()).isEqualTo("admin");
	}

	@Test
	public void searchUser() throws Exception {
		Map<String, Object> parameter = Maps.newHashMap();
		parameter.put("name", "管理员");
		List<User> result = userDao.search(parameter);
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getLoginName()).isEqualTo("admin");
	}

	@Test
	public void createAndDeleteUser() throws Exception {
		// create
		int count = countRowsInTable("ss_user");
		User user = UserData.randomUser();
		userDao.save(user);
		Long id = user.getId();

		assertThat(countRowsInTable("ss_user")).isEqualTo(count + 1);
		User result = userDao.get(id);
		assertThat(result.getLoginName()).isEqualTo(user.getLoginName());

		// delete
		userDao.delete(id);
		assertThat(countRowsInTable("ss_user")).isEqualTo(count);
		assertThat(userDao.get(id)).isNull();
	}

}
