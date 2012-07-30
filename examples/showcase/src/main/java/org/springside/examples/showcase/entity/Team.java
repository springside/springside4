package org.springside.examples.showcase.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.google.common.collect.Lists;

/**
 * 团队.
 * 
 * @author calvin
 */
@Entity
@Table(name = "SS_TEAM")
public class Team extends IdEntity {

	private String name;
	private User master;
	private List<User> userList = Lists.newArrayList();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToOne
	public User getMaster() {
		return master;
	}

	public void setMaster(User master) {
		this.master = master;
	}

	@OneToMany
	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
}
