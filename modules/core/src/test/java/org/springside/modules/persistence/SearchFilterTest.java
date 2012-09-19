package org.springside.modules.persistence;

import static org.junit.Assert.*;

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

		SearchFilter nameFilter = filters.get("name");
		assertEquals(Operator.EQ, nameFilter.operator);
		assertEquals("name", nameFilter.fieldName);
		assertEquals("foo", nameFilter.value);

		SearchFilter ageFilter = filters.get("age");
		assertEquals(Operator.LT, ageFilter.operator);
		assertEquals("age", ageFilter.fieldName);
		assertEquals("1", ageFilter.value);
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
