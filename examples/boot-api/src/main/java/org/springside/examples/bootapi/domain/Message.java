package org.springside.examples.bootapi.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.ToStringBuilder;

// JPA实体类的标识
@Entity
public class Message {

	// JPA 主键标识
	@Id
	public Long id;

	@ManyToOne
	@JoinColumn(name = "receiver_id")
	public Account receiver;

	public String message;

	public Date recieveDate;

	public Message(Account receiver, String message) {
		this.receiver = receiver;
		this.message = message;
		this.recieveDate = new Date();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
