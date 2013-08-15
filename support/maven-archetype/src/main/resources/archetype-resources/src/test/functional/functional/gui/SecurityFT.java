#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.functional.gui;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openqa.selenium.By;
import ${package}.functional.BaseSeleniumTestCase;

/**
 * 系统安全控制的功能测试, 测试主要用户故事.
 * 
 * @author calvin
 */
public class SecurityFT extends BaseSeleniumTestCase {

	/**
	 * 测试匿名用户访问系统时的行为.
	 */
	@Test
	public void anonymousUserAccessSystem() {
		//访问退出登录页面,退出之前的登录
		s.open("/logout");
		s.waitForTitleContains("登录页");

		//访问任意页面会跳转到登录界面
		s.open("/task");
		s.waitForTitleContains("登录页");
	}

	/**
	 * 测试普通用户访问管理员的用户管理功能时代行为。
	 */
	@Test
	public void userTryToManageUsers() {
		loginAsUserIfNecessary();
		s.open("/admin/user");
		assertEquals("Error 401 Unauthorized", s.getTitle());
	}

	/**
	 * 登录错误的用户名密码.
	 */
	@Test
	public void loginWithWrongPassword() {
		s.open("/logout");
		s.type(By.name("username"), "wrongUser");
		s.type(By.name("password"), "WrongPassword");
		s.check(By.name("rememberMe"));
		s.click(By.id("submit_btn"));

		s.waitForTitleContains("登录页");
		assertTrue(s.isTextPresent("登录失败，请重试."));
	}
}
