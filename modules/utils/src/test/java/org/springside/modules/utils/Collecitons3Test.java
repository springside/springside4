/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.utils;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

public class Collecitons3Test {

	@Test
	public void convertElementPropertyToString() {
		TestBean3 bean1 = new TestBean3();
		bean1.setId(1);
		TestBean3 bean2 = new TestBean3();
		bean2.setId(2);

		List list = Lists.newArrayList(bean1, bean2);

		assertThat(Collections3.extractToString(list, "id", ",")).isEqualTo("1,2");
	}

	@Test
	public void convertElementPropertyToList() {
		TestBean3 bean1 = new TestBean3();
		bean1.setId(1);
		TestBean3 bean2 = new TestBean3();
		bean2.setId(2);

		List list = Lists.newArrayList(bean1, bean2);
		List result = Collections3.extractToList(list, "id");
		assertThat(result).containsOnly(1, 2);
	}

	@Test
	public void convertCollectionToString() {
		List<String> list = Lists.newArrayList("aa", "bb");
		String result = Collections3.convertToString(list, ",");
		assertThat(result).isEqualTo("aa,bb");

		result = Collections3.convertToString(list, "<li>", "</li>");
		assertThat(result).isEqualTo("<li>aa</li><li>bb</li>");
	}

	public static class TestBean3 {

		private int id;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
	}

}
