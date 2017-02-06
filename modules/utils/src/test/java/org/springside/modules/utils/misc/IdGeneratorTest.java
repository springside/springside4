/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.utils.misc;

import org.junit.Test;

public class IdGeneratorTest {

	@Test
	public void demo() {
		System.out.println("uuid: " + IdGenerator.uuid());
		System.out.println("uuid2:" + IdGenerator.uuid2());
		System.out.println("randomLong:  " + IdGenerator.randomLong());
		System.out.println("randomBase64:" + IdGenerator.randomBase64(7));
	}
}
