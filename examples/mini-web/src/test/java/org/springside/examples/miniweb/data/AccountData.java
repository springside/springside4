package org.springside.examples.miniweb.data;

import java.util.List;

import org.springside.examples.miniweb.entity.account.Group;
import org.springside.examples.miniweb.entity.account.Permission;
import org.springside.examples.miniweb.entity.account.User;
import org.springside.modules.test.data.RandomData;

import com.google.common.collect.Lists;

/**
 * Account相关实体测试数据生成.
 * 
 * @author calvin
 */
public class AccountData {

	public static final String DEFAULT_PASSWORD = "123456";

	private static List<Group> defaultGroupList = null;

	private static List<String> defaultPermissionList = null;

	public static User getRandomUser() {
		String userName = RandomData.randomName("User");

		User user = new User();
		user.setLoginName(userName);
		user.setName(userName);
		user.setPassword(DEFAULT_PASSWORD);
		user.setEmail(userName + "@springside.org.cn");

		return user;
	}

	public static User getRandomUserWithOneGroup() {
		User user = getRandomUser();
		user.getGroupList().add(getRandomDefaultGroup());
		return user;
	}

	public static Group getRandomGroup() {
		Group group = new Group();
		group.setName(RandomData.randomName("Group"));
		return group;
	}

	public static Group getRandomGroupWithPermissions() {
		Group group = getRandomGroup();
		group.getPermissionList().addAll(getRandomDefaultPermissionList());
		return group;
	}

	public static List<Group> getDefaultGroupList() {
		if (defaultGroupList == null) {
			defaultGroupList = Lists.newArrayList();
			defaultGroupList.add(new Group(1L, "管理员"));
			defaultGroupList.add(new Group(2L, "用户"));
		}
		return defaultGroupList;
	}

	public static Group getRandomDefaultGroup() {
		return RandomData.randomOne(getDefaultGroupList());
	}

	public static List<String> getDefaultPermissionList() {
		if (defaultPermissionList == null) {
			defaultPermissionList = Lists.newArrayList();
			for (Permission permission : Permission.values()) {
				defaultPermissionList.add(permission.value);
			}
		}
		return defaultPermissionList;
	}

	public static List<String> getRandomDefaultPermissionList() {
		return RandomData.randomSome(getDefaultPermissionList());
	}
}
