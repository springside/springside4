package org.springside.modules.utils;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.junit.Test;

public class PropertiesLoaderTest {

	@Test
	public void multiProperty() throws IOException {
		Properties p = new PropertiesLoader("classpath:/test1.properties", "classpath:/test2.properties")
				.getProperties();

		assertEquals("1", p.getProperty("p1"));
		// value in test2 will override the value in test1
		assertEquals("10", p.getProperty("p2"));
		assertEquals("3", p.getProperty("p3"));
	}

	@Test
	public void notExistProperty() throws IOException {
		PropertiesLoader pl = new PropertiesLoader("classpath:/notexist.properties");
		try {
			assertNull(pl.getProperty("notexist"));
			fail("should fail here");
		} catch (NoSuchElementException e) {
		}
		assertEquals("defaultValue", pl.getProperty("notexist", "defaultValue"));
	}

	@Test
	public void integerDoubleAndBooleanProperty() {
		PropertiesLoader pl = new PropertiesLoader("classpath:/test1.properties", "classpath:/test2.properties");

		assertEquals(new Integer(1), pl.getInteger("p1"));
		try {
			pl.getInteger("notExist");
			fail("should fail here");
		} catch (NoSuchElementException e) {
		}
		assertEquals(new Integer(100), pl.getInteger("notExist", 100));

		assertEquals(new Boolean(true), pl.getBoolean("p4"));
		assertEquals(new Boolean(true), pl.getBoolean("p4", true));
		try {
			pl.getBoolean("notExist");
			fail("should fail here");
		} catch (NoSuchElementException e) {
		}
		assertEquals(new Boolean(true), pl.getBoolean("notExist", true));
	}

	@Test
	public void systemProperty() throws IOException {
		System.setProperty("p1", "sys");
		PropertiesLoader pl = new PropertiesLoader("classpath:/test1.properties", "classpath:/test2.properties");
		assertEquals("sys", pl.getProperty("p1"));
		System.clearProperty("p1");
	}
}
