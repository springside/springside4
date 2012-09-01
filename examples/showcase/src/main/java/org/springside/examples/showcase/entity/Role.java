package org.springside.examples.showcase.entity;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 角色.
 * 
 * @author calvin
 */
@Entity
@Table(name = "SS_ROLE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Role extends IdEntity {

	private String name;

	private String permissions;

	public Role() {
	}

	public Role(Long id) {
		this.id = id;
	}

	@Column(nullable = false, unique = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPermissions() {
		return permissions;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}

	@Transient
	public List<String> getPermissionList() {
		return Arrays.asList(StringUtils.split(permissions, ","));
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
