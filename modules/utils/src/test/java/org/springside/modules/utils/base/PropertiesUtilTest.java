package org.springside.modules.utils.base;

import static org.assertj.core.api.Assertions.*;

import java.util.Properties;

import org.junit.Test;

public class PropertiesUtilTest {

	@Test
	public void loadProperties() {
		Properties p1 = PropertiesUtil.loadFromFile("classpath://application.properties");
		assertThat(p1.get("springside.min")).isEqualTo("1");
		assertThat(p1.get("springside.max")).isEqualTo("10");

		Properties p2 = PropertiesUtil.loadFromString("springside.min=1\nspringside.max=10");
		assertThat(p2.get("springside.min")).isEqualTo("1");
		assertThat(p2.get("springside.max")).isEqualTo("10");
	}

}
