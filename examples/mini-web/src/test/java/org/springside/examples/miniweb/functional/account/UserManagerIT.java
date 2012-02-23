package org.springside.examples.miniweb.functional.account;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springside.examples.miniweb.data.AccountData;
import org.springside.examples.miniweb.entity.account.Group;
import org.springside.examples.miniweb.entity.account.User;
import org.springside.examples.miniweb.functional.BaseFunctionalTestCase;
import org.springside.examples.miniweb.functional.Gui;
import org.springside.examples.miniweb.functional.Gui.UserColumn;
import org.springside.modules.utils.Collections3;
import org.springside.modules.utils.Threads;

/**
 * 用户管理的功能测试, 测试页面JavaScript及主要用户故事流程.
 * 
 * @author calvin
 */
public class UserManagerIT extends BaseFunctionalTestCase {

	/**
	 * 查看用户列表.
	 */
	@Test
	public void viewUserList() {
		s.clickTo(By.linkText(Gui.MENU_USER));
		WebElement table = s.findElement(By.xpath("//table[@id='contentTable']"));
		assertEquals("admin", s.getTable(table, 1, UserColumn.LOGIN_NAME.ordinal()));
		assertEquals("Admin", s.getTable(table, 1, UserColumn.NAME.ordinal()));
		assertEquals("管理员, 用户", s.getTable(table, 1, UserColumn.GROUPS.ordinal()));
	}

	/**
	 * 创建用户.
	 */
	@Test
	public void createUser() {
		//打开新增用户页面
		s.clickTo(By.linkText(Gui.MENU_USER));
		s.clickTo(By.linkText("增加新用户"));

		//生成待输入的测试用户数据
		User user = AccountData.getRandomUserWithGroup();

		//输入数据
		s.type(By.id("loginName"), user.getLoginName());
		s.type(By.id("name"), user.getName());
		s.type(By.id("password"), user.getPassword());
		s.type(By.id("passwordConfirm"), user.getPassword());
		for (Group group : user.getGroupList()) {
			s.check(By.id("checkedGroupIds-" + group.getId()));
		}
		s.clickTo(By.xpath(Gui.BUTTON_SUBMIT));

		//校验结果
		assertTrue(s.isTextPresent("保存用户成功"));
		verifyUser(user);
	}

	/**
	 * 校验用户数据的工具函数.
	 */
	private void verifyUser(User user) {
		s.type(By.name("filter_EQS_loginName"), user.getLoginName());
		s.clickTo(By.xpath(Gui.BUTTON_SEARCH));
		s.clickTo(By.linkText("修改"));

		assertEquals(user.getLoginName(), s.getText(By.id("loginName")));
		assertEquals(user.getName(), s.getText(By.id("name")));

		for (Group group : user.getGroupList()) {
			assertTrue(s.isChecked(By.id("checkedGroupIds-" + group.getId())));
		}

		List<Group> uncheckGroupList = Collections3.subtract(AccountData.getDefaultGroupList(), user.getGroupList());
		for (Group group : uncheckGroupList) {
			assertFalse(s.isChecked(By.id("checkedGroupIds-" + group.getId())));
		}
	}

	/**
	 * 创建用户时的输入校验测试. 
	 */
	@Test
	public void inputValidateUser() {
		s.clickTo(By.linkText(Gui.MENU_USER));
		s.clickTo(By.linkText("增加新用户"));

		s.type(By.id("loginName"), "admin");
		s.type(By.id("name"), "");
		s.type(By.id("password"), "a");
		s.type(By.id("passwordConfirm"), "abc");
		s.type(By.id("email"), "abc");

		s.clickTo(By.xpath(Gui.BUTTON_SUBMIT));

		Threads.sleep(2000);

		WebElement table = s.findElement(By.xpath("//form/table"));
		assertEquals("用户登录名已存在", s.getTable(table, 0, 1));
		assertEquals("必选字段", s.getTable(table, 1, 1));
		assertEquals("请输入一个长度最少是 3 的字符串", s.getTable(table, 2, 1));
		assertEquals("输入与上面相同的密码", s.getTable(table, 3, 1));
		assertEquals("请输入正确格式的电子邮件", s.getTable(table, 4, 1));
	}

}
