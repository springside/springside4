package org.springside.examples.bootservice.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

// JPA实体类的标识
@Entity
@Table(name = "ss_task")
public class Task extends IdEntity {

	public String title;
	public String description;

	// 基于user_id列的多对一关系定义.
	@ManyToOne
	@JoinColumn(name = "user_id")
	public User user;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
