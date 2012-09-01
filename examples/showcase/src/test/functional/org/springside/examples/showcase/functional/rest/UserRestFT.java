package org.springside.examples.showcase.functional.rest;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import org.springside.examples.showcase.functional.BaseFunctionalTestCase;
import org.springside.examples.showcase.webservice.rest.UserDTO;

public class UserRestFT extends BaseFunctionalTestCase {

	private final RestTemplate restTemplate = new RestTemplate();

	private static String resoureUrl;

	@BeforeClass
	public static void initUrl() {
		resoureUrl = baseUrl + "/api/v1/user";
	}

	@Test
	public void getUser() {
		//as xml
		UserDTO user = restTemplate.getForObject(resoureUrl + "/{id}.xml", UserDTO.class, 1L);
		assertEquals("admin", user.getLoginName());

		//as json
		user = restTemplate.getForObject(resoureUrl + "/{id}.json", UserDTO.class, 1L);
		assertEquals("admin", user.getLoginName());
	}
}
