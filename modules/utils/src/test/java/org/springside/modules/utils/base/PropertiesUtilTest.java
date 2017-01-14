package org.springside.modules.utils.base;

import static org.assertj.core.api.Assertions.*;

import java.util.Properties;

import org.junit.Test;
import org.springside.modules.utils.base.PropertiesUtil.PropertiesListener;
import org.springside.modules.utils.number.RandomUtil;

public class PropertiesUtilTest {

	@Test
	public void systemProperty() {
		String name = "ss.test" + RandomUtil.nextInt();

		Boolean result0 = PropertiesUtil.booleanSystemProperty(name);
		assertThat(result0).isNull();

		Boolean result1 = PropertiesUtil.booleanSystemProperty(name, null);
		assertThat(result1).isNull();

		Boolean result3 = PropertiesUtil.booleanSystemProperty(name, Boolean.TRUE);
		assertThat(result3).isTrue();

		System.setProperty(name, "true");

		Boolean result5 = PropertiesUtil.booleanSystemProperty(name, Boolean.FALSE);
		assertThat(result5).isTrue();

		System.clearProperty(name);

		/// int
		Integer result6 = PropertiesUtil.intSystemProperty(name);
		assertThat(result6).isNull();

		result6 = PropertiesUtil.intSystemProperty(name, 1);
		assertThat(result6).isEqualTo(1);

		System.setProperty(name, "2");
		result6 = PropertiesUtil.intSystemProperty(name, 1);
		assertThat(result6).isEqualTo(2);

		System.clearProperty(name);

		///// long
		Long result7 = PropertiesUtil.longSystemProperty(name);
		assertThat(result7).isNull();

		result7 = PropertiesUtil.longSystemProperty(name, 1L);
		assertThat(result7).isEqualTo(1L);

		System.setProperty(name, "2");
		result7 = PropertiesUtil.longSystemProperty(name, 1L);
		assertThat(result7).isEqualTo(2L);

		System.clearProperty(name);

		///// doulbe
		Double result8 = PropertiesUtil.doubleSystemProperty(name);
		assertThat(result8).isNull();

		result8 = PropertiesUtil.doubleSystemProperty(name, 1.1);
		assertThat(result8).isEqualTo(1.1);

		System.setProperty(name, "2.1");
		result8 = PropertiesUtil.doubleSystemProperty(name, 1.1);
		assertThat(result8).isEqualTo(2.1);

		System.clearProperty(name);

		///// String
		String result9 = PropertiesUtil.stringSystemProperty(name);
		assertThat(result9).isNull();

		result9 = PropertiesUtil.stringSystemProperty(name, "1.1");
		assertThat(result9).isEqualTo("1.1");

		System.setProperty(name, "2.1");
		result9 = PropertiesUtil.stringSystemProperty(name, "1.1");
		assertThat(result9).isEqualTo("2.1");

		System.clearProperty(name);

	}

	@Test
	public void stringSystemProperty() {
		String name = "ss.test" + RandomUtil.nextInt();
		String envName = "ss_test" + RandomUtil.nextInt();

		// default 值
		String result = PropertiesUtil.stringSystemProperty(name, envName, "123");
		assertThat(result).isEqualTo("123");

		// env值
		String result2 = PropertiesUtil.stringSystemProperty(name, "HOME", "123");
		assertThat(result2).isNotEqualTo("123");

		// system properties值
		System.setProperty(name, "456");
		String result3 = PropertiesUtil.stringSystemProperty(name, envName, "123");
		assertThat(result3).isEqualTo("456");

		try {
			// 非法字符
			String result4 = PropertiesUtil.stringSystemProperty(name, name, "123");
			fail("should fail before");
		} catch (Exception e) {
			assertThat(e).isInstanceOf(IllegalArgumentException.class);
		}

		System.clearProperty(name);
	}

	@Test
	public void intSystemProperty() {
		String name = "ss.test" + RandomUtil.nextInt();
		String envName = "ss_test" + RandomUtil.nextInt();

		// default 值
		int result = PropertiesUtil.intSystemProperty(name, envName, 123);
		assertThat(result).isEqualTo(123);

		// env值没有数字类型的，忽略

		// system properties值
		System.setProperty(name, "456");
		int result3 = PropertiesUtil.intSystemProperty(name, envName, 123);
		assertThat(result3).isEqualTo(456);

		System.clearProperty(name);
	}

	@Test
	public void longSystemProperty() {
		String name = "ss.test" + RandomUtil.nextInt();
		String envName = "ss_test" + RandomUtil.nextInt();

		// default 值
		long result = PropertiesUtil.longSystemProperty(name, envName, 123L);
		assertThat(result).isEqualTo(123L);

		// env值没有数字类型的，忽略

		// system properties值
		System.setProperty(name, "456");
		long result3 = PropertiesUtil.longSystemProperty(name, envName, 123L);
		assertThat(result3).isEqualTo(456L);

		System.clearProperty(name);
	}

	@Test
	public void doubleSystemProperty() {
		String name = "ss.test" + RandomUtil.nextInt();
		String envName = "ss_test" + RandomUtil.nextInt();

		// default 值
		double result = PropertiesUtil.doubleSystemProperty(name, envName, 123d);
		assertThat(result).isEqualTo(123d);

		// env值没有数字类型的，忽略

		// system properties值
		System.setProperty(name, "456");
		double result3 = PropertiesUtil.doubleSystemProperty(name, envName, 123d);
		assertThat(result3).isEqualTo(456d);

		System.clearProperty(name);
	}

	@Test
	public void booleanSystemProperty() {
		String name = "ss.test" + RandomUtil.nextInt();
		String envName = "ss_test" + RandomUtil.nextInt();

		// default 值
		boolean result = PropertiesUtil.booleanSystemProperty(name, envName, true);
		assertThat(result).isTrue();

		// env值没有boolean类型的，忽略

		// system properties值
		System.setProperty(name, "true");
		boolean result3 = PropertiesUtil.booleanSystemProperty(name, envName, false);
		assertThat(result3).isTrue();

		System.clearProperty(name);
	}

	@Test
	public void loadProperties() {
		Properties p1 = PropertiesUtil.loadFromFile("classpath://application.properties");
		assertThat(p1.get("springside.min")).isEqualTo("1");
		assertThat(p1.get("springside.max")).isEqualTo("10");

		Properties p2 = PropertiesUtil.loadFromString("springside.min=1\nspringside.max=10");
		assertThat(p2.get("springside.min")).isEqualTo("1");
		assertThat(p2.get("springside.max")).isEqualTo("10");
	}

	@Test
	public void listenableProperties() {
		String name = "ss.test" + RandomUtil.nextInt();

		TestPropertiesListener listener = new TestPropertiesListener(name);
		PropertiesUtil.registerSystemPropertiesListener(listener);

		System.setProperty(name, "haha");

		assertThat(listener.newValue).isEqualTo("haha");
	}

	public static class TestPropertiesListener extends PropertiesListener {

		public TestPropertiesListener(String propertyName) {
			super(propertyName);
		}

		public String newValue;

		@Override
		public void onChange(String propertyName, String value) {
			newValue = value;
		}

	};

}
