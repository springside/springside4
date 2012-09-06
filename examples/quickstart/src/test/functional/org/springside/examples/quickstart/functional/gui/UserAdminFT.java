package org.springside.examples.quickstart.functional.gui;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springside.examples.quickstart.functional.BaseSeleniumTestCase;
import org.springside.modules.test.category.Smoke;

public class UserAdminFT extends BaseSeleniumTestCase {

	@BeforeClass
	public static void loginAsAdmin() {
		s.open("/logout");
		s.type(By.name("username"), "admin");
		s.type(By.name("password"), "admin");
		s.click(By.id("submit_btn"));
	}

	@AfterClass
	public static void logout() {
		s.open("/logout");
	}

	/**
	 * 浏览用户列表.
	 */
	@Test
	@Category(Smoke.class)
	public void viewUserList() {
		s.open("/admin/user");
		WebElement table = s.findElement(By.id("contentTable"));
		assertEquals("admin", s.getTable(table, 0, 0));
		assertEquals("user", s.getTable(table, 1, 0));
	}

	@Test
	public void editUser() {
		s.open("/admin/user/update/2");
		s.type(By.id("name"), "Kevin");
		s.type(By.id("plainPassword"), "user2");
		s.type(By.id("confirmPassword"), "user2");
		s.click(By.id("submit_btn"));

		assertTrue("没有成功消息", s.isTextPresent("更新用户user成功"));
		WebElement table = s.findElement(By.id("contentTable"));
		assertEquals("Kevin", s.getTable(table, 1, 1));
	}

	@Test
	public void deleteUser() {
		s.open("/admin/user/delete/2");
		assertTrue("没有成功消息", s.isTextPresent("删除用户user成功"));
	}
}
