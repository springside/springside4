/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.utils;

import org.junit.Test;

public class IdentitiesTest {

	@Test
	public void demo() {
		System.out.println("uuid: " + Ids.uuid());
		System.out.println("uuid2:" + Ids.uuid2());
		System.out.println("randomLong:  " + Ids.randomLong());
		System.out.println("randomBase62:" + Ids.randomBase62(7));
	}
}
