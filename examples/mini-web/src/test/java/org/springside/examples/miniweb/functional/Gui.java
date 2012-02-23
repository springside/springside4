package org.springside.examples.miniweb.functional;

/**
 * 定义页面元素的常量.
 */
public class Gui {

	public static final String BUTTON_LOGIN = "//input[@value='登录']";
	public static final String BUTTON_SUBMIT = "//input[@value='提交']";
	public static final String BUTTON_SEARCH = "//input[@value='搜索']";

	public static final String MENU_USER = "帐号列表";
	public static final String MENU_GROUP = "权限组列表";

	public enum UserColumn {
		LOGIN_NAME, NAME, EMAIL, GROUPS, OPERATIONS
	}

	public enum GroupColumn {
		NAME, PERMISSIONS, OPERATIONS
	}
}
