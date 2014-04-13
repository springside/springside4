/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.demos.cache.ehcache;

import static org.assertj.core.api.Assertions.*;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.spring.SpringContextTestCase;

/**
 * 演示Ehcache的配置.
 * 
 * 配置见applicationContext-ehcache.xml与ehcache.xml
 * 
 * @author calvin
 */
@ContextConfiguration(locations = { "/cache/applicationContext-ehcache.xml" })
public class EhcacheDemo extends SpringContextTestCase {

	private static final String CACHE_NAME = "demoCache";

	@Autowired
	private CacheManager ehcacheManager;

	private Cache cache;

	@Test
	public void demo() {

		cache = ehcacheManager.getCache(CACHE_NAME);

		String key = "foo";
		String value = "boo";

		put(key, value);
		Object result = get(key);

		assertThat(result).isEqualTo(value);
	}

	public Object get(String key) {
		Element element = cache.get(key);
		return element.getObjectValue();
	}

	public void put(String key, Object value) {
		Element element = new Element(key, value);
		cache.put(element);
	}
}
