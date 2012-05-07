package org.springside.examples.showcase.functional.webservice.rs;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springside.examples.showcase.functional.BaseFunctionalTestCase;
import org.springside.examples.showcase.webservice.rs.client.UserResourceClient;
import org.springside.examples.showcase.webservice.rs.dto.UserDTO;
import org.springside.modules.test.category.Smoke;

import com.sun.jersey.api.client.UniformInterfaceException;

public class UserResourceServiceIT extends BaseFunctionalTestCase {

	private static UserResourceClient client;

	@BeforeClass
	public static void setUpClient() {
		client = new UserResourceClient();
		client.setBaseUrl(baseUrl + "/rs");
	}

	/**
	 * 演示与Shiro的结合.
	 */
	@Test
	@Category(Smoke.class)
	public void getAllUser() {
		List<UserDTO> userList = client.getAllUser("admin", "admin");
		assertTrue(userList.size() >= 6);
		UserDTO admin = userList.iterator().next();
		assertEquals("admin", admin.getLoginName());
	}

	/**
	 * 演示与Shiro的结合.
	 */
	@Test
	public void getAllUserWithWrongPassword() {
		//wrong password
		try {
			client.getAllUser("admin", "wrongPassword");
		} catch (UniformInterfaceException e) {
			assertEquals(401, e.getResponse().getStatus());
			return;
		}
		fail("Exception should happen");
	}

	/**
	 * 演示与Shiro的结合.
	 */
	@Test
	public void getAllUserWithoutPermission() {
		try {
			client.getAllUser("user", "user");
		} catch (UniformInterfaceException e) {
			assertEquals(401, e.getResponse().getStatus());
			return;
		}
		fail("Exception should happen");
	}

	/**
	 * 演示QueryParam与不同格式不同返回内容的Response.
	 */
	@Test
	public void searchUserHtml() {
		String html = client.searchUserReturnHtml("Admin");
		assertEquals("<div>Admin, your mother call you...</div>", html);
	}

	/**
	 * 演示QueryParam与不同格式不同返回内容的Response.
	 * @throws Exception
	 */
	@Test
	public void searchUserJson() throws Exception {
		UserDTO admin = client.searchUserReturnJson("Admin");
		assertEquals("admin", admin.getLoginName());
	}

	@Test
	public void multiPart() {
		String result = client.multipart("foo", "a good guy");
		assertEquals("foo:a good guy", result);
	}
}
