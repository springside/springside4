package org.springside.examples.showcase.functional.account;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.By;
import org.springside.examples.showcase.functional.BaseSeleniumTestCase;
import org.springside.modules.test.category.Smoke;

/**
 * 用户管理的功能测试.
 * 
 * @author calvin
 */
public class UserManagerFT extends BaseSeleniumTestCase {

	@Test
	@Category(Smoke.class)
	public void list() {
		s.open("/");
		s.click(By.linkText("帐号管理"));
		loginAsAdminIfNecessary();
		assertEquals("Showcase示例:综合演示用例", s.getTitle());
	}

	@Test
	@Category(Smoke.class)
	public void editUser() {
		s.open("/");
		s.click(By.linkText("帐号管理"));
		loginAsAdminIfNecessary();

		s.click(By.id("editLink-user"));

		//点击提交按钮
		s.type(By.name("name"), "user_foo");
		s.getSelect(By.name("status")).selectByValue("disabled");
		s.click(By.id("submit"));

		//重新进入用户修改页面, 检查最后修改者
		s.click(By.id("editLink-user"));
		assertEquals("user_foo", s.getValue(By.name("name")));
		assertEquals("disabled", s.getSelect(By.name("status")).getFirstSelectedOption().getText());

		//恢复原有值
		s.type(By.name("name"), "user");
		s.getSelect(By.name("status")).selectByValue("enabled");
		s.click(By.id("submit"));
	}

	private void loginAsAdminIfNecessary() {
		//修改用户需要登录管理员权限
		if ("Showcase示例:登录页".equals(s.getTitle())) {
			s.type(By.name("username"), "admin");
			s.type(By.name("password"), "admin");
			s.click(By.id("submit"));
		}
	}
}
