/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.mapper;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 测试Jackson对Object,Map,List,数组,枚举,日期类等的持久化.
 * 更多测试见showcase中的JsonDemo.
 * 
 * @author calvin
 */
public class JsonMapperTest {

	private static JsonMapper binder = JsonMapper.nonDefaultMapper();

	/**
	 * 序列化对象/集合到Json字符串.
	 */
	@Test
	public void toJson() throws Exception {
		// Bean
		TestBean bean = new TestBean("A");
		String beanString = binder.toJson(bean);
		System.out.println("Bean:" + beanString);
		assertThat(beanString).isEqualTo("{\"name\":\"A\"}");

		// Map
		Map<String, Object> map = Maps.newLinkedHashMap();
		map.put("name", "A");
		map.put("age", 2);
		String mapString = binder.toJson(map);
		System.out.println("Map:" + mapString);
		assertThat(mapString).isEqualTo("{\"name\":\"A\",\"age\":2}");

		// List<String>
		List<String> stringList = Lists.newArrayList("A", "B", "C");
		String listString = binder.toJson(stringList);
		System.out.println("String List:" + listString);
		assertThat(listString).isEqualTo("[\"A\",\"B\",\"C\"]");

		// List<Bean>
		List<TestBean> beanList = Lists.newArrayList(new TestBean("A"), new TestBean("B"));
		String beanListString = binder.toJson(beanList);
		System.out.println("Bean List:" + beanListString);
		assertThat(beanListString).isEqualTo("[{\"name\":\"A\"},{\"name\":\"B\"}]");

		// Bean[]
		TestBean[] beanArray = new TestBean[] { new TestBean("A"), new TestBean("B") };
		String beanArrayString = binder.toJson(beanArray);
		System.out.println("Array List:" + beanArrayString);
		assertThat(beanArrayString).isEqualTo("[{\"name\":\"A\"},{\"name\":\"B\"}]");
	}

	/**
	 * 从Json字符串反序列化对象/集合.
	 */
	@Test
	public void fromJson() throws Exception {
		// Bean
		String beanString = "{\"name\":\"A\"}";
		TestBean bean = binder.fromJson(beanString, TestBean.class);
		System.out.println("Bean:" + bean);

		// Map
		String mapString = "{\"name\":\"A\",\"age\":2}";
		Map<String, Object> map = binder.fromJson(mapString, HashMap.class);
		System.out.println("Map:");
		for (Entry<String, Object> entry : map.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}

		// List<String>
		String listString = "[\"A\",\"B\",\"C\"]";
		List<String> stringList = binder.getMapper().readValue(listString, List.class);
		System.out.println("String List:");
		for (String element : stringList) {
			System.out.println(element);
		}

		// List<Bean>
		String beanListString = "[{\"name\":\"A\"},{\"name\":\"B\"}]";
		List<TestBean> beanList = binder.getMapper().readValue(beanListString, new TypeReference<List<TestBean>>() {
		});
		System.out.println("Bean List:");
		for (TestBean element : beanList) {
			System.out.println(element);
		}
	}

	/**
	 * 测试传入空对象,空字符串,Empty的集合,"null"字符串的结果.
	 */
	@Test
	public void nullAndEmpty() {
		// toJson测试 //

		// Null Bean
		TestBean nullBean = null;
		String nullBeanString = binder.toJson(nullBean);
		assertThat(nullBeanString).isEqualTo("null");

		// Empty List
		List<String> emptyList = Lists.newArrayList();
		String emptyListString = binder.toJson(emptyList);
		assertThat(emptyListString).isEqualTo("[]");

		// fromJson测试 //

		// Null String for Bean
		TestBean nullBeanResult = binder.fromJson(null, TestBean.class);
		assertThat(nullBeanResult).isNull();

		nullBeanResult = binder.fromJson("null", TestBean.class);
		assertThat(nullBeanResult).isNull();

		// Null/Empty String for List
		List nullListResult = binder.fromJson(null, List.class);
		assertThat(nullListResult).isNull();

		nullListResult = binder.fromJson("null", List.class);
		assertThat(nullListResult).isNull();

		nullListResult = binder.fromJson("[]", List.class);
		assertThat(nullListResult).isEmpty();
	}

	/**
	 * 测试三种不同的Binder.
	 */
	@Test
	public void threeTypeBinders() {
		// 打印全部属性
		JsonMapper normalBinder = new JsonMapper();
		TestBean bean = new TestBean("A");
		assertThat(normalBinder.toJson(bean)).isEqualTo(
				"{\"name\":\"A\",\"defaultValue\":\"hello\",\"nullValue\":null}");

		// 不打印nullValue属性
		JsonMapper nonNullBinder = JsonMapper.nonEmptyMapper();
		assertThat(nonNullBinder.toJson(bean)).isEqualTo("{\"name\":\"A\",\"defaultValue\":\"hello\"}");

		// 不打印默认值未改变的nullValue与defaultValue属性
		JsonMapper nonDefaultBinder = JsonMapper.nonDefaultMapper();
		assertThat(nonDefaultBinder.toJson(bean)).isEqualTo("{\"name\":\"A\"}");
	}

	public static class TestBean {

		private String name;
		private String defaultValue = "hello";
		private String nullValue = null;

		public TestBean() {
		}

		public TestBean(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}

		public String getNullValue() {
			return nullValue;
		}

		public void setNullValue(String nullValue) {
			this.nullValue = nullValue;
		}

		@Override
		public String toString() {
			return "TestBean [defaultValue=" + defaultValue + ", name=" + name + ", nullValue=" + nullValue + "]";
		}
	}

}
