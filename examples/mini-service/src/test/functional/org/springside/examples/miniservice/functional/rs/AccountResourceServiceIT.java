package org.springside.examples.miniservice.functional.rs;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springside.examples.miniservice.data.AccountData;
import org.springside.examples.miniservice.entity.User;
import org.springside.examples.miniservice.functional.BaseFunctionalTestCase;
import org.springside.examples.miniservice.functional.category.Smoke;
import org.springside.examples.miniservice.webservice.dto.DepartmentDTO;
import org.springside.examples.miniservice.webservice.dto.UserDTO;
import org.springside.examples.miniservice.webservice.rs.client.AccountResourceClient;

import com.sun.jersey.api.client.UniformInterfaceException;

public class AccountResourceServiceIT extends BaseFunctionalTestCase {

	private static AccountResourceClient client;

	@BeforeClass
	public static void setUpClient() throws Exception {
		client = new AccountResourceClient();
		client.setBaseUrl(baseUrl + "/rs");
	}

	@Test
	@Category(Smoke.class)
	public void getDeptartmentDetail() {
		DepartmentDTO department = client.getDepartmentDetail(1L);
		assertEquals("Development", department.getName());
		assertEquals(2, department.getUserList().size());
		assertEquals("Jack", department.getUserList().get(0).getName());
	}

	@Test
	public void getDeptartmentDetailWithInvalidId() {
		try {
			client.getDepartmentDetail(999L);
			fail("Should thrown exception while invalid id");
		} catch (UniformInterfaceException e) {
			assertEquals(404, e.getResponse().getStatus());
		}
	}

	@Test
	@Category(Smoke.class)
	public void searchUser() {
		List<UserDTO> result = client.searchUser(null, null);
		assertTrue(result.size() > 1);

		result = client.searchUser("user1", null);
		assertEquals(1, result.size());
	}

	@Test
	@Category(Smoke.class)
	public void createUser() {
		User user = AccountData.getRandomUser();
		UserDTO dto = new DozerBeanMapper().map(user, UserDTO.class);

		URI uri = client.createUser(dto);
		assertNotNull(uri);
		System.out.println("Created user uri:" + uri);
	}

	@Test
	public void createUserWithInvalidLoginName() {
		//必须值为空
		User user = AccountData.getRandomUser();
		UserDTO dto = new DozerBeanMapper().map(user, UserDTO.class);
		dto.setLoginName(null);

		try {
			client.createUser(dto);
			fail("Should thrown exception while invalid id");
		} catch (UniformInterfaceException e) {
			assertEquals(400, e.getResponse().getStatus());
		}

		dto.setLoginName("user2");

		try {
			client.createUser(dto);
			fail("Should thrown exception while invalid id");
		} catch (UniformInterfaceException e) {
			assertEquals(400, e.getResponse().getStatus());
		}
	}
}
