package org.springside.examples.miniservice.entity;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * 部门.
 * 
 * @author calvin
 */
public class Department extends IdEntity {

	private String name;
	private User manager;
	private List<User> userList = Lists.newArrayList();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getManager() {
		return manager;
	}

	public void setManager(User manager) {
		this.manager = manager;
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
}
