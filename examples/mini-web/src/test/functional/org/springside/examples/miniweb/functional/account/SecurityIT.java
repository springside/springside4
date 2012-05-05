package org.springside.examples.miniweb.functional.account;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openqa.selenium.By;
import org.springside.examples.miniweb.functional.BaseFunctionalTestCase;
import org.springside.examples.miniweb.functional.Gui;
import org.springside.examples.miniweb.functional.Gui.UserColumn;

/**
 * 系统安全控制的功能测试, 测试主要用户故事.
 * 
 * @author calvin
 */
public class SecurityIT extends BaseFunctionalTestCase {

	/**
	 * 测试匿名用户访问系统时的行为.
	 */
	@Test
	public void checkAnonymous() {
		//访问退出登录页面,退出之前的登录
		s.open("/logout");
		assertEquals("Mini-Web示例:登录页", s.getTitle());

		//访问任意页面会跳转到登录界面
		s.open("/account/user");
		assertEquals("Mini-Web示例:登录页", s.getTitle());
	}

	/**
	 * 只有用户权限组的操作员访问系统时的受限行为.
	 */
	@Test
	public void checkUserPermission() {
		//访问退出登录页面,退出之前的登录
		s.open("/logout");
		assertEquals("Mini-Web示例:登录页", s.getTitle());

		//登录普通用户
		s.type(By.name("username"), "user");
		s.type(By.name("password"), "user");
		s.click(By.id("submit"));

		//校验用户权限组的操作单元格只有查看
		s.click(Gui.MENU_USER);
		assertEquals("", s.getTable(By.id("contentTable"), 1, UserColumn.OPERATIONS.ordinal()));

		//强行访问无权限的url
		s.open("/account/user/update/1");
		assertTrue(s.getTitle().contains("403"));
		//重新退出
		s.open("/logout");
	}
}
