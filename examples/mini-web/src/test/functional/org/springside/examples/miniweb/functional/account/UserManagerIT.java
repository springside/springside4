package org.springside.examples.miniweb.functional.account;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springside.examples.miniweb.data.AccountData;
import org.springside.examples.miniweb.entity.account.Group;
import org.springside.examples.miniweb.entity.account.User;
import org.springside.examples.miniweb.functional.BaseFunctionalTestCase;
import org.springside.examples.miniweb.functional.Gui;
import org.springside.examples.miniweb.functional.Gui.UserColumn;
import org.springside.modules.test.category.Smoke;
import org.springside.modules.utils.Collections3;

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
	@Category(Smoke.class)
	public void viewUserList() {
		s.click(Gui.MENU_USER);
		WebElement table = s.findElement(By.id("contentTable"));
		assertEquals("admin", s.getTable(table, 0, UserColumn.LOGIN_NAME.ordinal()));
		assertEquals("Admin", s.getTable(table, 0, UserColumn.NAME.ordinal()));
		assertEquals("管理员, 用户", s.getTable(table, 0, UserColumn.GROUPS.ordinal()));
	}

	/**
	 * 创建用户.
	 */
	@Category(Smoke.class)
	@Test
	public void createUser() {
		//打开新增用户页面
		s.click(Gui.MENU_USER);
		s.click(By.linkText("创建用户"));

		//生成待输入的测试用户数据
		User user = AccountData.getRandomUserWithOneGroup();

		//输入数据
		s.type(By.id("loginName"), user.getLoginName());
		s.type(By.id("name"), user.getName());
		s.type(By.id("password"), user.getPassword());
		s.type(By.id("passwordConfirm"), user.getPassword());
		List<WebElement> checkBoxes = s.findElements(By.name("groupList"));
		for (Group group : user.getGroupList()) {
			for (WebElement checkBox : checkBoxes) {
				if (String.valueOf(group.getId()).equals(s.getValue(checkBox))) {
					s.check(checkBox);
				}
			}

		}
		s.click(By.id("submit"));

		//校验结果
		assertTrue(s.isTextPresent("创建用户" + user.getLoginName() + "成功"));
		verifyUser(user);
	}

	/**
	 * 校验用户数据的工具函数.
	 */
	private void verifyUser(User user) {

		s.click(By.id("editLink-" + user.getLoginName()));

		assertEquals(user.getLoginName(), s.getValue(By.id("loginName")));
		assertEquals(user.getName(), s.getValue(By.id("name")));

		List<WebElement> checkBoxes = s.findElements(By.name("groupList"));
		for (Group group : user.getGroupList()) {
			for (WebElement checkBox : checkBoxes) {
				if (String.valueOf(group.getId()).equals(s.getValue(checkBox))) {
					assertTrue(s.isChecked(checkBox));
				}
			}
		}

		List<Group> uncheckGroupList = Collections3.subtract(AccountData.getDefaultGroupList(), user.getGroupList());
		for (Group group : uncheckGroupList) {
			for (WebElement checkBox : checkBoxes) {
				if (String.valueOf(group.getId()).equals(s.getValue(checkBox))) {
					assertFalse(s.isChecked(checkBox));
				}
			}

		}
	}

	/**
	 * 创建用户时的输入校验测试. 
	 */
	@Test
	public void inputInValidateUser() {
		s.click(Gui.MENU_USER);
		s.click(By.linkText("创建用户"));

		s.type(By.id("loginName"), "admin");
		s.type(By.id("name"), "");
		s.type(By.id("password"), "a");
		s.type(By.id("passwordConfirm"), "abc");
		s.type(By.id("email"), "abc");

		s.click(By.id("submit"));
		assertEquals("用户登录名已存在", s.getText(By.xpath("//fieldset/div[2]/div/label")));
		assertEquals("必选字段", s.getText(By.xpath("//fieldset/div[3]/div/label")));
		assertEquals("请输入一个长度最少是 3 的字符串", s.getText(By.xpath("//fieldset/div[4]/div/label")));
		assertEquals("输入与上面相同的密码", s.getText(By.xpath("//fieldset/div[5]/div/label")));
		assertEquals("请输入正确格式的电子邮件", s.getText(By.xpath("//fieldset/div[6]/div/label")));
	}

}
