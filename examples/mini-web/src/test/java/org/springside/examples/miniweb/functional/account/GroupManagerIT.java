package org.springside.examples.miniweb.functional.account;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springside.examples.miniweb.data.AccountData;
import org.springside.examples.miniweb.entity.account.Group;
import org.springside.examples.miniweb.functional.BaseFunctionalTestCase;
import org.springside.examples.miniweb.functional.Gui;
import org.springside.examples.miniweb.functional.Gui.GroupColumn;
import org.springside.modules.utils.Collections3;

/**
 * 权限组管理的功能测试,测 试页面JavaScript及主要用户故事流程.
 * 
 * @author calvin
 */
public class GroupManagerIT extends BaseFunctionalTestCase {

	/**
	 * 查看权限组列表.
	 */
	@Test
	public void viewGroupList() {
		s.clickTo(By.linkText(Gui.MENU_GROUP));
		WebElement table = s.findElement(By.xpath("//table[@id='contentTable']"));
		assertEquals("管理员", s.getTable(table, 1, GroupColumn.NAME.ordinal()));
		assertEquals("查看用戶,修改用户,查看权限组,修改权限组", s.getTable(table, 1, GroupColumn.PERMISSIONS.ordinal()));
	}

	/**
	 * 创建权限组.
	 */
	@Test
	public void createGroup() {
		s.clickTo(By.linkText(Gui.MENU_GROUP));
		s.clickTo(By.linkText("增加新权限组"));

		//生成测试数据
		Group group = AccountData.getRandomGroupWithPermissions();

		//输入数据
		s.type(By.id("name"), group.getName());
		for (String permission : group.getPermissionList()) {
			s.check(By.id("checkedPermissions-" + permission));
		}
		s.clickTo(By.xpath(Gui.BUTTON_SUBMIT));

		//校验结果
		assertTrue(s.isTextPresent("保存权限组成功"));
		verifyGroup(group);
	}

	private void verifyGroup(Group group) {
		s.clickTo(By.linkText(Gui.MENU_GROUP));
		s.clickTo(By.id("editLink-" + group.getName()));

		assertEquals(group.getName(), s.getText(By.id("name")));

		for (String permission : group.getPermissionList()) {
			assertTrue(s.isChecked(By.id("checkedPermissions-" + permission)));
		}

		List<String> uncheckPermissionList = Collections3.subtract(AccountData.getDefaultPermissionList(),
				group.getPermissionList());
		for (String permission : uncheckPermissionList) {
			assertFalse(s.isChecked(By.id("checkedPermissions-" + permission)));
		}
	}
}
