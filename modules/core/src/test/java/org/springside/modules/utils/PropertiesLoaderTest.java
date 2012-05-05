package org.springside.modules.utils;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

public class PropertiesLoaderTest {

	@Test
	public void multiProperty() throws IOException {
		Properties p = new PropertiesLoader("classpath:/test1.properties", "classpath:/test2.properties")
				.getProperties();

		assertEquals("1", p.getProperty("p1"));
		//value in test2 will override the value in test1
		assertEquals("10", p.getProperty("p2"));
		assertEquals("3", p.getProperty("p3"));
	}

	@Test
	public void notExistProperty() throws IOException {
		Properties p = new PropertiesLoader("classpath:/notexist.properties").getProperties();
		assertNull(p.getProperty("notexist"));

		assertEquals("defaultValue", p.getProperty("notexist", "defaultValue"));
	}

	@Test
	public void integerAndBooleanProperty() {
		PropertiesLoader p = new PropertiesLoader("classpath:/test1.properties", "classpath:/test2.properties");

		assertEquals(new Integer(1), p.getInteger("p1"));
		assertEquals(new Integer(0), p.getInteger("notExist"));
		assertEquals(new Integer(100), p.getInteger("notExist", 100));

		assertEquals(new Boolean(true), p.getBoolean("p4"));
		assertEquals(new Boolean(false), p.getBoolean("notExist"));
		assertEquals(new Boolean(true), p.getBoolean("notExist", true));
	}

	@Test
	public void systemProperty() throws IOException {
		System.setProperty("p1", "sys");
		PropertiesLoader pl = new PropertiesLoader("classpath:/test1.properties", "classpath:/test2.properties");
		assertEquals("sys", pl.getProperty("p1"));
		System.clearProperty("p1");
	}
}
