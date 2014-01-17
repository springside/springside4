/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.quickstart.functional.gui;

import static org.assertj.core.api.Assertions.*;

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
		assertThat(s.getTable(table, 0, 0)).isEqualTo("admin");
		assertThat(s.getTable(table, 1, 0)).isEqualTo("user");
	}

	@Test
	public void editUser() {
		s.open("/admin/user/update/2");
		s.type(By.id("name"), "Kevin");
		s.type(By.id("plainPassword"), "user2");
		s.type(By.id("confirmPassword"), "user2");
		s.click(By.id("submit_btn"));

		assertThat(s.isTextPresent("更新用户user成功")).as("没有成功消息").isTrue();
		WebElement table = s.findElement(By.id("contentTable"));
		assertThat(s.getTable(table, 1, 1)).isEqualTo("Kevin");
	}

	@Test
	public void deleteUser() {
		s.open("/admin/user/delete/2");
		assertThat(s.isTextPresent("删除用户user成功")).as("没有成功消息").isTrue();
	}
}
