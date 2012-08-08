package org.springside.examples.showcase.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.google.common.collect.Lists;

/**
 * 团队.
 * 
 * @author calvin
 */
@Entity
@Table(name = "SS_TEAM")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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
	@JoinColumn(name = "MASTER_ID")
	public User getMaster() {
		return master;
	}

	public void setMaster(User master) {
		this.master = master;
	}

	@OneToMany(mappedBy = "team")
	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
}
