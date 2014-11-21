/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.demos.cache.memcached;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.cache.memcached.SpyMemcachedClient;
import org.springside.modules.test.spring.SpringContextTestCase;

import com.google.common.collect.Lists;

@ContextConfiguration(locations = { "/cache/applicationContext-memcached.xml", "/applicationContext.xml" })
public class MemcachedDemo extends SpringContextTestCase {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	@Test
	public void normal() {

		String key = "consumer:1";
		String value = "admin";

		spyMemcachedClient.set(key, 60 * 60 * 1, value);

		String result = spyMemcachedClient.get(key);
		assertThat(result).isEqualTo(value);

		spyMemcachedClient.delete(key);
		result = spyMemcachedClient.get(key);
		assertThat(result).isNull();
	}

	@Test
	public void safeDelete() {
		String key = "consumer:1";
		spyMemcachedClient.set(key, 60, "admin");
		assertThat(spyMemcachedClient.safeDelete(key)).isTrue();
		assertThat(spyMemcachedClient.safeDelete("consumer:1")).isFalse();
	}

	@Test
	public void getBulk() {

		String key1 = "consumer:1";
		String value1 = "admin";

		String key2 = "consumer:2";
		String value2 = "calvin";

		String key3 = "invalidKey";

		spyMemcachedClient.set(key1, 60 * 60 * 1, value1);
		spyMemcachedClient.set(key2, 60 * 60 * 1, value2);

		Map<String, String> result = spyMemcachedClient.getBulk(Lists.newArrayList(key1, key2));
		assertThat(result.get(key1)).isEqualTo(value1);
		assertThat(result.get(key2)).isEqualTo(value2);
		assertThat(result.get(key3)).isNull();
	}

	@Test
	public void incr() {
		String key = "incr_key";

		// 注意,incr返回的数值使用long表达
		long result = spyMemcachedClient.incr(key, 2, 1);
		assertThat(result).isEqualTo(1);
		// 注意,get返回的数值使用字符串表达
		assertThat((String) spyMemcachedClient.get(key)).isEqualTo("1");

		assertThat(spyMemcachedClient.incr(key, 2, 1)).isEqualTo(3);
		assertThat((String) spyMemcachedClient.get(key)).isEqualTo("3");

		key = "set_and_incr_key";
		// 注意,set中的数值必须使用字符串,后面的incr操作结果才会正确.
		spyMemcachedClient.set(key, 60 * 60 * 1, "1");
		assertThat(spyMemcachedClient.incr(key, 2, 1)).isEqualTo(3);

	}
}
