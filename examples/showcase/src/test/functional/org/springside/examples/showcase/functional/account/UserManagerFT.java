/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.functional.account;

import static org.assertj.core.api.Assertions.*;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springside.examples.showcase.functional.BaseSeleniumTestCase;
import org.springside.modules.test.category.Smoke;

/**
 * 用户管理的功能测试.
 * 
 * @author calvin
 */
@Ignore("Ignore the GUI test first")
public class UserManagerFT extends BaseSeleniumTestCase {

	@Test
	@Category(Smoke.class)
	public void list() {
		s.open("/");
		s.click(By.linkText("帐号管理"));
		loginAsAdminIfNecessary();
		s.waitForTitleContains("综合演示用例");
		WebElement table = s.findElement(By.id("contentTable"));
		assertThat(s.getTable(table, 0, 1)).isEqualTo("管理员 ");
	}

	@Test
	@Category(Smoke.class)
	public void editUser() {
		s.open("/");
		s.click(By.linkText("帐号管理"));
		loginAsAdminIfNecessary();

		s.click(By.id("editLink-user"));

		// 点击提交按钮
		s.type(By.name("name"), "user_foo");
		s.check(By.id("status2"));
		s.click(By.id("submit_btn"));

		// 重新进入用户修改页面, 检查最后修改者
		s.click(By.id("editLink-user"));
		assertThat(s.getValue(By.name("name"))).isEqualTo("user_foo");
		assertThat(s.isChecked(By.id("status2"))).isTrue();

		// 恢复原有值
		s.type(By.name("name"), "user");
		s.check(By.id("status1"));
		s.click(By.id("submit_btn"));
	}

	private void loginAsAdminIfNecessary() {
		// 修改用户需要登录管理员权限
		if (s.getTitle().contains("登录页")) {
			s.type(By.name("username"), "admin");
			s.type(By.name("password"), "admin");
			s.click(By.id("submit_btn"));
		}
	}
}
