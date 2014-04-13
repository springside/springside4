/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.test.security.shiro;

import static org.assertj.core.api.Assertions.*;

import org.apache.shiro.SecurityUtils;
import org.junit.Test;

public class ShiroTestUtilsTest {

	@Test
	public void mockSubject() {
		ShiroTestUtils.mockSubject("foo");
		assertThat(SecurityUtils.getSubject().isAuthenticated()).isTrue();
		assertThat(SecurityUtils.getSubject().getPrincipal()).isEqualTo("foo");

		ShiroTestUtils.clearSubject();

		ShiroTestUtils.mockSubject("bar");
		assertThat(SecurityUtils.getSubject().isAuthenticated()).isTrue();
		assertThat(SecurityUtils.getSubject().getPrincipal()).isEqualTo("bar");

	}

}
