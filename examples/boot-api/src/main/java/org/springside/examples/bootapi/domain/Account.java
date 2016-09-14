package org.springside.examples.bootapi.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.ToStringBuilder;

// JPA实体类的标识
@Entity
public class Account {

	// JPA 主键标识, 策略为由数据库生成主键
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	public String email;
	public String name;
	public String hashPassword;

	public Account() {

	}

	public Account(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
