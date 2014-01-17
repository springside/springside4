/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.persistence;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.Test;
import org.springside.modules.persistence.SearchFilter.Operator;

import com.google.common.collect.Maps;

public class SearchFilterTest {

	@Test
	public void normal() {
		// linkedHashMap保证顺序
		Map<String, Object> params = Maps.newLinkedHashMap();
		params.put("EQ_name", "foo");
		params.put("LT_age", "1");

		Map<String, SearchFilter> filters = SearchFilter.parse(params);

		SearchFilter nameFilter = filters.get("EQ_name");
		assertThat(nameFilter.operator).isEqualTo(Operator.EQ);
		assertThat(nameFilter.fieldName).isEqualTo("name");
		assertThat(nameFilter.value).isEqualTo("foo");

		SearchFilter ageFilter = filters.get("LT_age");
		assertThat(ageFilter.operator).isEqualTo(Operator.LT);
		assertThat(ageFilter.fieldName).isEqualTo("age");
		assertThat(ageFilter.value).isEqualTo("1");
	}

	@Test
	public void emptyValue() {
		// linkedHashMap保证顺序
		Map<String, Object> params = Maps.newLinkedHashMap();
		params.put("EQ_name", "foo");
		params.put("LT_age", null);
		params.put("LT_mail", "");

		Map<String, SearchFilter> filters = SearchFilter.parse(params);
		assertThat(filters).hasSize(1).containsKey("EQ_name");
	}

	@Test
	public void wrongName() {

		try {
			Map<String, Object> params = Maps.newLinkedHashMap();
			params.put("EQ", "foo");

			SearchFilter.parse(params);
			fail("should fail with wrong name");
		} catch (IllegalArgumentException e) {
		}

		try {
			Map<String, Object> params = Maps.newLinkedHashMap();
			params.put("EQ_", "foo");

			SearchFilter.parse(params);
			fail("should fail with wrong name");
		} catch (IllegalArgumentException e) {
		}

		try {
			Map<String, Object> params = Maps.newLinkedHashMap();
			params.put("ABC_name", "foo");

			SearchFilter.parse(params);
			fail("should fail with wrong operator name");
		} catch (IllegalArgumentException e) {
		}
	}
}
