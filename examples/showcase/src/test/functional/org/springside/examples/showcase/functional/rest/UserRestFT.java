package org.springside.examples.showcase.functional.rest;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springside.examples.showcase.functional.BaseFunctionalTestCase;
import org.springside.examples.showcase.webservice.rest.UserDTO;
import org.springside.modules.web.Servlets;

public class UserRestFT extends BaseFunctionalTestCase {

	private final RestTemplate restTemplate = new RestTemplate();

	private static String resoureUrl;

	@BeforeClass
	public static void initUrl() {
		resoureUrl = baseUrl + "/api/v1/user";
	}

	/**
	 * 演示restTemplate中如何设置Headers.
	 * 演示json与xml格式数据.
	 */
	@Test
	public void getUser() {
		//设置Http Basic参数
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(com.google.common.net.HttpHeaders.AUTHORIZATION, Servlets.encodeHttpBasic("admin", "admin"));
		HttpEntity<?> requestEntity = new HttpEntity(requestHeaders);

		//as xml
		HttpEntity<UserDTO> response = restTemplate.exchange(resoureUrl + "/{id}.xml", HttpMethod.GET, requestEntity,
				UserDTO.class, 1L);
		assertEquals("admin", response.getBody().getLoginName());
		assertEquals(new Long(1), response.getBody().getTeamId());

		//as json
		response = restTemplate.exchange(resoureUrl + "/{id}.json", HttpMethod.GET, requestEntity, UserDTO.class, 1L);
		assertEquals("admin", response.getBody().getLoginName());
		assertEquals(new Long(1), response.getBody().getTeamId());
	}

	/**
	 * 演示restTemplate中如何设置Headers.
	 * 演示json与xml格式数据.
	 */
	@Test
	public void authWithHttpBasic() {
		//设置Http Basic参数
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(com.google.common.net.HttpHeaders.AUTHORIZATION,
				Servlets.encodeHttpBasic("admin", "wrongpassword"));
		HttpEntity<?> requestEntity = new HttpEntity(requestHeaders);

		try {
			restTemplate.exchange(resoureUrl + "/{id}.xml", HttpMethod.GET, requestEntity, UserDTO.class, 1L);
			fail("Get should fail with error username/password");
		} catch (HttpClientErrorException e) {
			assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
		}
	}
}
