package org.springside.examples.showcase.functional.rest;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import org.springside.examples.showcase.functional.BaseFunctionalTestCase;
import org.springside.examples.showcase.webservice.rest.UserDTO;

/**
 * 对基于JAX-RS的实现Restful的测试
 * 
 * @author calvin
 */
public class UserJaxRsFT extends BaseFunctionalTestCase {

	private final RestTemplate restTemplate = new RestTemplate();

	private static String resoureUrl;

	@BeforeClass
	public static void initUrl() {
		resoureUrl = baseUrl + "/cxf/jaxrs/user";
	}

	@Test
	public void getUser() {
		UserDTO user = restTemplate.getForObject(resoureUrl + "/{id}.json", UserDTO.class, 1L);
		assertEquals("admin", user.getLoginName());
		assertEquals(new Long(1), user.getTeamId());

		user = restTemplate.getForObject(resoureUrl + "/{id}.xml", UserDTO.class, 1L);
		assertEquals("admin", user.getLoginName());
		assertEquals(new Long(1), user.getTeamId());
	}
}
