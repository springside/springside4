package org.springside.examples.bootservice.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

// JPA实体类的标识
@Entity
@Table(name = "ss_user")
public class User extends IdEntity {

	private String name;

	public User() {
	}

	public User(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}