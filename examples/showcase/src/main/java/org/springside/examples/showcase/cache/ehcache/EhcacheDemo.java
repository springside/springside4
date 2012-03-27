package org.springside.examples.showcase.cache.ehcache;

import static org.junit.Assert.*;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.spring.SpringContextTestCase;

/**
 * 本地缓存策略,使用EhCache, 支持限制总数, Idle time/LRU失效, 持久化到磁盘等功能.
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
	public void normal() {

		cache = ehcacheManager.getCache(CACHE_NAME);

		String key = "foo";
		String value = "boo";

		put(key, value);
		Object result = get(key);

		assertEquals(value, result);
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
