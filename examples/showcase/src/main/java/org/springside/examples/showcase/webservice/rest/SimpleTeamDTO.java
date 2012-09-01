package org.springside.examples.showcase.webservice.rest;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class SimpleTeamDTO {

	private String name;

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
