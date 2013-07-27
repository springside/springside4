#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.functional.gui;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openqa.selenium.By;
import ${package}.functional.BaseSeleniumTestCase;

public class RegisterFT extends BaseSeleniumTestCase {

	@Test
	public void register() {
		//注册用户
		s.open("/logout");
		s.click(By.linkText("注册"));

		s.type(By.id("loginName"), "user2");
		s.type(By.id("name"), "Kevin");
		s.type(By.id("plainPassword"), "user2");
		s.type(By.id("confirmPassword"), "user2");

		s.click(By.id("submit_btn"));

		//跳转到登录页
		s.waitForTitleContains("登录页");
		assertEquals("user2", s.getValue(By.name("username")));

		s.type(By.name("password"), "user2");
		s.click(By.id("submit_btn"));

		//登陆成功
		s.waitForTitleContains("任务管理");

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
