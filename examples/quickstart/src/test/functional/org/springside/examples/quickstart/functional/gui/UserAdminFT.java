package org.springside.examples.quickstart.functional.gui;

import static org.junit.Assert.*;

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

}
