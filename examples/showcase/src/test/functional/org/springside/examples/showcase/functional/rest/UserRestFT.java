/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.functional.rest;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springside.examples.showcase.functional.BaseFunctionalTestCase;
import org.springside.examples.showcase.webservice.rest.UserDTO;
import org.springside.modules.test.category.Smoke;
import org.springside.modules.web.Servlets;

import com.google.common.collect.Lists;

/**
 * 演示Rest Service的客户端，包含：
 * 1. 使用exchange()函数与使用对Header操作的两种方式
 * 2. JDK HttpConnection与Apache HttpClient4两种底层
 * 3. 验证JSON与XML两种编码
 * 4. 验证与Shiro结合的HttpBasic验证
 * 
 * @author calvin
 */
public class UserRestFT extends BaseFunctionalTestCase {

	private static String resourceUrl = baseUrl + "/api/secure/v1/user";

	private RestTemplate jdkTemplate;
	private RestTemplate httpClientRestTemplate;
	private HttpComponentsClientHttpRequestFactory httpClientRequestFactory;

	@Before
	public void initRestTemplate() {
		// 默认使用JDK Connection
		jdkTemplate = new RestTemplate();
		// (optional)设置20秒超时
		((SimpleClientHttpRequestFactory) jdkTemplate.getRequestFactory()).setConnectTimeout(20000);
		((SimpleClientHttpRequestFactory) jdkTemplate.getRequestFactory()).setReadTimeout(20000);

		// 设置使用HttpClient4.0
		httpClientRestTemplate = new RestTemplate();
		httpClientRequestFactory = new HttpComponentsClientHttpRequestFactory();
		// (optional)设置20秒超时
		httpClientRequestFactory.setConnectTimeout(20000);

		httpClientRestTemplate.setRequestFactory(httpClientRequestFactory);

		// 设置处理HttpBasic Header的Interceptor
		ClientHttpRequestInterceptor interceptor = new HttpBasicInterceptor("admin", "admin");
		httpClientRestTemplate.setInterceptors(Lists.newArrayList(interceptor));
	}

	@After
	public void destoryClient() throws Exception {
		// 退出时关闭HttpClient4连接池中的连接
		httpClientRequestFactory.destroy();
	}

	/**
	 * 演示使用原始的exchange()方法来设置Headers.
	 * 演示xml格式数据.
	 * 演示jdk connection.
	 */
	@Test
	public void getUserAsXML() {
		// 设置Http Basic参数
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(com.google.common.net.HttpHeaders.AUTHORIZATION, Servlets.encodeHttpBasic("admin", "admin"));
		System.out.println("Http header is" + requestHeaders);
		HttpEntity<?> requestEntity = new HttpEntity(requestHeaders);

		try {
			HttpEntity<UserDTO> response = jdkTemplate.exchange(resourceUrl + "/{id}.xml", HttpMethod.GET,
					requestEntity, UserDTO.class, 1L);
			assertThat(response.getBody().getLoginName()).isEqualTo("admin");
			assertThat(response.getBody().getName()).isEqualTo("管理员");
			assertThat(response.getBody().getTeamId()).isEqualTo(1);

			// 直接取出XML串
			HttpEntity<String> xml = jdkTemplate.exchange(resourceUrl + "/{id}.xml", HttpMethod.GET, requestEntity,
					String.class, 1L);
			System.out.println("xml output is " + xml.getBody());
		} catch (HttpStatusCodeException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * 演示使用ClientHttpRequestInterceptor设置header, see #initRestTemplate()
	 * 演示json格式数据.
	 * 演示使用Apache Http client4.
	 */
	@Test
	@Category(Smoke.class)
	public void getUserAsJson() {
		UserDTO user = httpClientRestTemplate.getForObject(resourceUrl + "/{id}.json", UserDTO.class, 1L);
		assertThat(user.getLoginName()).isEqualTo("admin");
		assertThat(user.getName()).isEqualTo("管理员");
		assertThat(user.getTeamId()).isEqualTo(1);

		try {
			// 直接取出JSON串
			String json = httpClientRestTemplate.getForObject(resourceUrl + "/{id}.json", String.class, 1L);
			System.out.println("json output is " + json);
		} catch (HttpStatusCodeException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * 验证与Shiro的HttpBasic的结合
	 */
	@Test
	public void authWithHttpBasic() {
		// 设置Http Basic参数
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(com.google.common.net.HttpHeaders.AUTHORIZATION,
				Servlets.encodeHttpBasic("admin", "wrongpassword"));
		HttpEntity<?> requestEntity = new HttpEntity(requestHeaders);

		try {
			jdkTemplate.exchange(resourceUrl + "/{id}.xml", HttpMethod.GET, requestEntity, UserDTO.class, 1L);
			fail("Get should fail with error username/password");
		} catch (HttpStatusCodeException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		}
	}

	/**
	 * 处理HttpBasicHeader的Interceptor
	 */
	public static class HttpBasicInterceptor implements ClientHttpRequestInterceptor {

		private final String user;
		private final String password;

		public HttpBasicInterceptor(String user, String password) {
			this.user = user;
			this.password = password;
		}

		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
				throws IOException {
			request.getHeaders().set(com.google.common.net.HttpHeaders.AUTHORIZATION,
					Servlets.encodeHttpBasic(user, password));
			return execution.execute(request, body);
		}
	}
}
