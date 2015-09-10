/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.utils;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.junit.Test;

public class PropertiesLoaderTest {

	@Test
	public void multiProperty() throws IOException {
		Properties p = new PropertiesLoader("classpath:/test1.properties", "classpath:/test2.properties")
				.getProperties();

		assertThat(p.getProperty("p1")).isEqualTo("1");
		// value in test2 will override the value in test1
		assertThat(p.getProperty("p2")).isEqualTo("10");
		assertThat(p.getProperty("p3")).isEqualTo("3");
	}

	@Test
	public void notExistProperty() throws IOException {
		PropertiesLoader pl = new PropertiesLoader("classpath:/notexist.properties");
		try {
			assertThat(pl.getProperty("notexist")).isNull();
			failBecauseExceptionWasNotThrown(NoSuchElementException.class);
		} catch (NoSuchElementException e) {
		}
		assertThat(pl.getProperty("notexist", "defaultValue")).isEqualTo("defaultValue");
	}

	@Test
	public void integerDoubleAndBooleanProperty() {
		PropertiesLoader pl = new PropertiesLoader("classpath:/test1.properties", "classpath:/test2.properties");

		assertThat(pl.getInteger("p1")).isEqualTo(new Integer(1));
		try {
			pl.getInteger("notExist");
			failBecauseExceptionWasNotThrown(NoSuchElementException.class);
		} catch (NoSuchElementException e) {
		}
		assertThat(pl.getInteger("notExist", 100)).isEqualTo(new Integer(100));

		assertThat(pl.getBoolean("p4")).isEqualTo(new Boolean(true));
		assertThat(pl.getBoolean("p4", true)).isEqualTo(new Boolean(true));

		try {
			pl.getBoolean("notExist");
			failBecauseExceptionWasNotThrown(NoSuchElementException.class);
		} catch (NoSuchElementException e) {
		}
		assertThat(pl.getBoolean("notExist", true)).isEqualTo(new Boolean(true));
	}

	@Test
	public void systemProperty() throws IOException {
		System.setProperty("p1", "sys");
		PropertiesLoader pl = new PropertiesLoader("classpath:/test1.properties", "classpath:/test2.properties");
		assertThat(pl.getProperty("p1")).isEqualTo("sys");
		System.clearProperty("p1");
	}
}
