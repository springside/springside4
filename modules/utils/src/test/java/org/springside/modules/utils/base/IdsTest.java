/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.utils.base;

import org.junit.Test;
import org.springside.modules.utils.base.Ids;

public class IdsTest {

	@Test
	public void demo() {
		System.out.println("uuid: " + Ids.uuid());
		System.out.println("uuid2:" + Ids.uuid2());
		System.out.println("randomLong:  " + Ids.randomLong());
		System.out.println("randomBase64:" + Ids.randomBase64(7));
	}
}
