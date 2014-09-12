/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.webservice.soap.response.dto;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springside.examples.showcase.webservice.soap.WsConstants;

/**
 * Web Service传输User信息的DTO.
 * 
 * 只传输外部接口需要的属性.使用JAXB 2.0的annotation标注JAVA-XML映射,尽量使用默认约定.
 * 
 * @author calvin
 */
@XmlRootElement
@XmlType(name = "User", namespace = WsConstants.NS)
public class UserDTO {

	private Long id;
	private String loginName;
	private String name;
	private String email;
	private TeamDTO team;

	public Long getId() {
		return id;
	}

	public void setId(Long value) {
		id = value;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String value) {
		loginName = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String value) {
		name = value;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String value) {
		email = value;
	}

	public TeamDTO getTeam() {
		return team;
	}

	public void setTeam(TeamDTO team) {
		this.team = team;
	}

	/**
	 * 重新实现toString()函数方便在日志中打印DTO信息.
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
