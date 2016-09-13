package org.springside.examples.bootapi.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.ToStringBuilder;

// JPA实体类的标识
@Entity
public class Book {

	public static final String STATUS_IDLE = "idle";
	public static final String STATUS_REQUEST = "request";
	public static final String STATUS_OUT = "out";

	// JPA 主键标识, 策略为由数据库生成主键
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	public String doubanId;

	public String title;

	public String url;

	public String status;

	@ManyToOne
	@JoinColumn(name = "owner_id")
	public Account owner;

	public Date onboardDate;

	@ManyToOne
	@JoinColumn(name = "borrower_id")
	public Account borrower;

	public Date borrowDate;

	public Book() {
	}

	public Book(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
