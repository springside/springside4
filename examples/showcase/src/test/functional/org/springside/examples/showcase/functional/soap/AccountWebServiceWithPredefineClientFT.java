/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.functional.soap;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springside.examples.showcase.data.UserData;
import org.springside.examples.showcase.entity.User;
import org.springside.examples.showcase.functional.BaseFunctionalTestCase;
import org.springside.examples.showcase.webservice.soap.AccountSoapService;
import org.springside.examples.showcase.webservice.soap.response.GetUserResult;
import org.springside.examples.showcase.webservice.soap.response.SearchUserResult;
import org.springside.examples.showcase.webservice.soap.response.base.IdResult;
import org.springside.examples.showcase.webservice.soap.response.base.WSResult;
import org.springside.examples.showcase.webservice.soap.response.dto.UserDTO;
import org.springside.modules.mapper.BeanMapper;
import org.springside.modules.test.category.Smoke;

/**
 * AccountService Web服务的功能测试, 测试主要的接口调用.
 * 
 * 使用在Spring applicaitonContext.xml中用<jaxws:client/>，根据AccountWebService接口创建的Client.
 * 
 * 集中在User相关接口.
 * 
 * @author calvin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(locations = { "/applicationContext-soap-client.xml" })
public class AccountWebServiceWithPredefineClientFT extends BaseFunctionalTestCase {

	@Autowired
	private AccountSoapService accountWebServiceClient;

	/**
	 * 测试获取用户.
	 */
	@Test
	@Category(Smoke.class)
	public void getUser() {
		GetUserResult response = accountWebServiceClient.getUser(1L);
		assertThat(response.getUser().getLoginName()).isEqualTo("admin");
	}

	/**
	 * 测试搜索用户
	 */
	@Test
	public void searchUser() {
		SearchUserResult response = accountWebServiceClient.searchUser(null, null);

		assertThat(response.getUserList().size() >= 4).isTrue();
		assertThat(response.getUserList().get(0).getName()).isEqualTo("管理员");
	}

	/**
	 * 测试创建用户.
	 */
	@Test
	public void createUser() {
		User user = UserData.randomUser();
		UserDTO userDTO = BeanMapper.map(user, UserDTO.class);

		IdResult response = accountWebServiceClient.createUser(userDTO);
		assertThat(response.getId()).isNotNull();
		GetUserResult response2 = accountWebServiceClient.getUser(response.getId());
		assertThat(response2.getUser().getLoginName()).isEqualTo(user.getLoginName());
	}

	/**
	 * 测试创建用户,使用错误的登录名.
	 */
	@Test
	public void createUserWithInvalidLoginName() {
		User user = UserData.randomUser();
		UserDTO userDTO = BeanMapper.map(user, UserDTO.class);

		// 登录名为空
		userDTO.setLoginName(null);
		IdResult response = accountWebServiceClient.createUser(userDTO);
		assertThat(response.getCode()).isEqualTo(WSResult.PARAMETER_ERROR);

		// 登录名重复
		userDTO.setLoginName("user");
		response = accountWebServiceClient.createUser(userDTO);
		assertThat(response.getCode()).isEqualTo(WSResult.PARAMETER_ERROR);
	}
}
