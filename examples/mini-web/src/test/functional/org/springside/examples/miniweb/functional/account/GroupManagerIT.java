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
		s.click(Gui.MENU_GROUP);
		WebElement table = s.findElement(By.xpath("//table[@id='contentTable']"));
		assertEquals("管理员", s.getTable(table, 1, GroupColumn.NAME.ordinal()));
		assertEquals("查看用戶,修改用户,查看权限组,修改权限组", s.getTable(table, 1, GroupColumn.PERMISSIONS.ordinal()));
	}

	/**
	 * 创建权限组.
	 */
	@Test
	public void createGroup() {
		s.click(Gui.MENU_GROUP);
		s.click(By.linkText("创建权限组"));

		//生成测试数据
		Group group = AccountData.getRandomGroupWithPermissions();

		//输入数据
		s.type(By.id("name"), group.getName());

		List<WebElement> checkBoxes = s.findElements(By.name("permissionList"));
		for (String permission : group.getPermissionList()) {
			for (WebElement checkBox : checkBoxes) {
				if (permission.equals(s.getValue(checkBox))) {
					s.check(checkBox);
				}
			}
		}

		s.click(By.id("submit"));

		//校验结果
		assertTrue(s.isTextPresent("创建权限组" + group.getName() + "成功"));
		verifyGroup(group);
	}

	private void verifyGroup(Group group) {
		s.click(Gui.MENU_GROUP);
		s.click(By.id("editLink-" + group.getName()));
		assertEquals(group.getName(), s.getValue(By.id("name")));

		List<WebElement> checkBoxes = s.findElements(By.name("permissionList"));
		for (String permission : group.getPermissionList()) {
			for (WebElement checkBox : checkBoxes) {
				if (permission.equals(s.getValue(checkBox))) {
					assertTrue(s.isChecked(checkBox));
				}
			}
		}

		List<String> uncheckPermissionList = Collections3.subtract(AccountData.getDefaultPermissionList(),
				group.getPermissionList());
		for (String permission : uncheckPermissionList) {
			for (WebElement checkBox : checkBoxes) {
				if (permission.equals(s.getValue(checkBox))) {
					assertFalse(s.isChecked(checkBox));
				}
			}
		}
	}
}
