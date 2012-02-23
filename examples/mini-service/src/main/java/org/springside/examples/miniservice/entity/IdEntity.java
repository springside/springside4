package org.springside.examples.miniservice.entity;

/**
 * 统一定义id的entity基类.
 * 
 * @author calvin
 */
public abstract class IdEntity {

	protected Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
