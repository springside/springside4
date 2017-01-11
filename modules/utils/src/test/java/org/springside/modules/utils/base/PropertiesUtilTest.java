package org.springside.modules.utils.base;

import static org.assertj.core.api.Assertions.*;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.springside.modules.utils.base.PropertiesUtil.PropertiesListener;
import org.springside.modules.utils.number.RandomUtil;

public class PropertiesUtilTest {

	@Test
	public void readBoolean() {
		String name = "ss.test" + RandomUtil.nextInt();

		boolean result = PropertiesUtil.getBooleanDefaultFalse(name);
		assertThat(result).isFalse();

		Boolean result0 = PropertiesUtil.getBooleanDefaultNull(name);
		assertThat(result0).isNull();

		Boolean result1 = PropertiesUtil.getBoolean(name, null);
		assertThat(result1).isNull();

		boolean result2 = PropertiesUtil.getBoolean(name, false);
		assertThat(result2).isFalse();

		Boolean result3 = PropertiesUtil.getBoolean(name, Boolean.TRUE);
		assertThat(result3).isTrue();

		System.setProperty(name, "true");
		Boolean result4 = PropertiesUtil.getBoolean(name, false);
		assertThat(result4).isTrue();
	}

	@Test
	public void readString() {
		String name = "ss.test" + RandomUtil.nextInt();
		String envName = "ss_test" + RandomUtil.nextInt();

		// default 值
		String result = PropertiesUtil.readString(name, envName, "123");
		assertThat(result).isEqualTo("123");

		// env值
		String result2 = PropertiesUtil.readString(name, "HOME", "123");
		assertThat(result2).isNotEqualTo("123");

		// system properties值
		System.setProperty(name, "456");
		String result3 = PropertiesUtil.readString(name, envName, "123");
		assertThat(result3).isEqualTo("456");

		try {
			// 非法字符
			String result4 = PropertiesUtil.readString(name, name, "123");
			Assert.fail("should fail before");
		} catch (Exception e) {
			assertThat(e).isInstanceOf(IllegalArgumentException.class);
		}
		
		System.clearProperty(name);
	}

	
	@Test
	public void readInt() {
		String name = "ss.test" + RandomUtil.nextInt();
		String envName = "ss_test" + RandomUtil.nextInt();

		// default 值
		int result = PropertiesUtil.readInt(name, envName, 123);
		assertThat(result).isEqualTo(123);

		// env值没有数字类型的，忽略
		

		// system properties值
		System.setProperty(name, "456");
		int result3 = PropertiesUtil.readInt(name, envName, 123);
		assertThat(result3).isEqualTo(456);
		
		System.clearProperty(name);
	}
	
	@Test
	public void readLong() {
		String name = "ss.test" + RandomUtil.nextInt();
		String envName = "ss_test" + RandomUtil.nextInt();

		// default 值
		long result = PropertiesUtil.readLong(name, envName, 123L);
		assertThat(result).isEqualTo(123L);

		// env值没有数字类型的，忽略
		

		// system properties值
		System.setProperty(name, "456");
		long result3 = PropertiesUtil.readLong(name, envName, 123L);
		assertThat(result3).isEqualTo(456L);
		
		System.clearProperty(name);
	}
	
	
	@Test
	public void readDouble() {
		String name = "ss.test" + RandomUtil.nextInt();
		String envName = "ss_test" + RandomUtil.nextInt();

		// default 值
		double result = PropertiesUtil.readDouble(name, envName, 123d);
		assertThat(result).isEqualTo(123d);

		// env值没有数字类型的，忽略
		

		// system properties值
		System.setProperty(name, "456");
		double result3 = PropertiesUtil.readDouble(name, envName, 123d);
		assertThat(result3).isEqualTo(456d);
		
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
