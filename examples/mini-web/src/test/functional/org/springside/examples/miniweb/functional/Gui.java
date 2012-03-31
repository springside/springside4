package org.springside.examples.miniweb.functional;

/**
 * 定义页面元素的常量.
 */
public class Gui {

	public static final String MENU_USER = "帐号列表";
	public static final String MENU_GROUP = "权限组列表";

	//定义表格内容，避免表格内容顺序变动引起case的大崩溃。
	public enum UserColumn {
		LOGIN_NAME, NAME, EMAIL, GROUPS, OPERATIONS
	}

	public enum GroupColumn {
		NAME, PERMISSIONS, OPERATIONS
	}
}
