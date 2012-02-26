package org.springside.modules.memcached;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.spring.SpringContextTestCase;
import org.springside.modules.utils.Threads;

@ContextConfiguration(locations = { "/applicationContext-memcached.xml" })
public class SpyMemcachedClientTest extends SpringContextTestCase {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	@Test
	public void normal() {

		String key = "consumer:1";
		String value = "admin";

		spyMemcachedClient.set(key, 60 * 60 * 1, value);
		Threads.sleep(1000);
		String result = spyMemcachedClient.get(key);
		assertEquals(value, result);

		spyMemcachedClient.delete(key);
		Threads.sleep(1000);
		result = spyMemcachedClient.get(key);
		assertNull(result);
	}

}
