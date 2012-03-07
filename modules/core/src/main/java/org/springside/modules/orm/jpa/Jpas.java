package org.springside.modules.orm.jpa;

import org.hibernate.Hibernate;

public class Jpas {
	private Jpas() {

	}

	public static void initLazyProperty(Object proxyed) {
		Hibernate.initialize(proxyed);
	}
}
