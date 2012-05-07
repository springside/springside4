package org.springside.modules.orm.jpa;

import org.hibernate.Hibernate;

public class Jpas {

	public static void initLazyProperty(Object proxyed) {
		Hibernate.initialize(proxyed);
	}
}
