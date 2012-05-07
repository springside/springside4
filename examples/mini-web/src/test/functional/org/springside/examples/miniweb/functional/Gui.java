package org.springside.examples.miniweb.functional;

import org.openqa.selenium.By;

/**
 * 定义页面元素的常量.
 */
public class Gui {

	public static final By MENU_USER = By.linkText("帐号列表");
	public static final By MENU_GROUP = By.linkText("权限组列表");

	//定义表格内容，避免表格内容顺序变动引起case的大崩溃。
	public enum UserColumn {
		LOGIN_NAME, NAME, EMAIL, GROUPS, OPERATIONS
	}

	public enum GroupColumn {
		NAME, PERMISSIONS, OPERATIONS
	}
}
