package org.springside.modules.utils.base;

import static org.assertj.core.api.Assertions.*;

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
