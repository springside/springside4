package org.springside.examples.quickstart.entity;

import javax.persistence.Entity;

@Entity
public class Task extends IdEntity {

	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
