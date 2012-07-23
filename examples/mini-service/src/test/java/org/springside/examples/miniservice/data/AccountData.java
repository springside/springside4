package org.springside.examples.miniservice.data;

import org.springside.examples.miniservice.entity.Department;
import org.springside.examples.miniservice.entity.User;
import org.springside.modules.test.data.RandomData;

/**
 * 用户测试数据生成.
 * 
 * @author calvin
 */
public class AccountData {

	public static Department randomDepartment() {
		String departmentName = RandomData.randomName("Department");
		User manager = randomUser();

		Department department = new Department();
		department.setName(departmentName);

		department.setManager(manager);
		department.getUserList().add(manager);

		return department;
	}

	public static User randomUser() {
		String userName = RandomData.randomName("User");
		Department department = defaultDepartment();

		User user = new User();
		user.setLoginName(userName);
		user.setName(userName);
		user.setPassword("123456");
		user.setEmail(userName + "@springside.org.cn");
		user.setDepartment(department);

		return user;
	}

	public static Department defaultDepartment() {
		Department department = new Department();
		department.setId(1L);
		return department;
	}
}
