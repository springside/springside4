package org.springside.examples.showcase.data;

import org.springside.examples.showcase.common.entity.Role;
import org.springside.examples.showcase.common.entity.User;
import org.springside.modules.test.data.RandomData;

/**
 * 用户测试数据生成.
 * 
 * @author calvin
 */
public class UserData {

	public static User getRandomUser() {
		String userName = RandomData.randomName("User");

		User user = new User();
		user.setLoginName(userName);
		user.setName(userName);
		user.setPlainPassword("123456");
		user.setEmail(userName + "@springside.org.cn");

		return user;
	}

	public static User getRandomUserWithAdminRole() {
		User user = UserData.getRandomUser();
		Role adminRole = UserData.getAdminRole();
		user.getRoleList().add(adminRole);
		return user;
	}

	public static Role getAdminRole() {
		Role role = new Role();
		role.setId(1L);
		role.setName("Admin");

		return role;
	}
}
