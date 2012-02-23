package org.springside.examples.showcase.common.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 回复贴.
 * 
 * @author calvin
 */
@Entity
//子类标识字段值
@DiscriminatorValue("Reply")
public class Reply extends Post {

	private Subject subject;

	//与主题的多对一关系
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subject_id")
	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
