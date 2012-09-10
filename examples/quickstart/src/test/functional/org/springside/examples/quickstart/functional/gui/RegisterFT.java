package org.springside.examples.quickstart.functional.gui;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openqa.selenium.By;
import org.springside.examples.quickstart.functional.BaseSeleniumTestCase;

public class RegisterFT extends BaseSeleniumTestCase {

	@Test
	public void register() {
		//注册用户
		s.open("/logout");
		s.open("/register");
		s.type(By.id("loginName"), "user2");
		s.type(By.id("name"), "Kevin");
		s.type(By.id("plainPassword"), "user2");
		s.type(By.id("confirmPassword"), "user2");

		s.click(By.id("submit_btn"));

		//跳转到登录页
		assertEquals("QuickStart示例:登录页", s.getTitle());
		assertEquals("user2", s.getValue(By.name("username")));

		s.type(By.name("password"), "user2");
		s.click(By.id("submit_btn"));

		//登陆成功
		assertEquals("QuickStart示例:任务管理", s.getTitle());

		//退出用户
		s.open("/logout");
	}

	@Test
	public void inputInValidateValue() {
		s.open("/register");
		s.click(By.id("submit_btn"));

		assertEquals("必选字段", s.getText(By.xpath("//fieldset/div/div/span")));
	}

}
