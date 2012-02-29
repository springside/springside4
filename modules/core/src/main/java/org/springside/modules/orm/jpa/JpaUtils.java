package org.springside.modules.orm.jpa;

import org.hibernate.Hibernate;

public class JpaUtils {

	public static void initLazyProperty(Object proxyed) {
		Hibernate.initialize(proxyed);
	}
}
