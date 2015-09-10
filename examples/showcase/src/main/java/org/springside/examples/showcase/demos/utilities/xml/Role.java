/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.demos.utilities.xml;

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 使用JAXB2.0标注的待转换Java Bean.
 */
public class Role {
	private Long id;
	private String name;

	public Role() {
	}

	public Role(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	@XmlAttribute
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@XmlAttribute
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
