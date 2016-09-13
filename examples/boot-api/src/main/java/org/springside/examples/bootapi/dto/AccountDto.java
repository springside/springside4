package org.springside.examples.bootapi.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class AccountDto {
	public Long id;
	public String email;
	public String name;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
