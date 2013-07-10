package org.springside.examples.showcase.demos.redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Utils {

	public static JedisPool createJedisPool(String defaultHost, int defaultPort, int defaultTimeout, int threadCount) {
		// merge default setting and system properties
		String host = System.getProperty("benchmark.host", defaultHost);
		String port = System.getProperty("benchmark.port", String.valueOf(defaultPort));
		String timeout = System.getProperty("benchmark.timeout", String.valueOf(defaultTimeout));

		// create jedis pool
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxActive(threadCount);
		poolConfig.setMaxIdle(threadCount);
		return new JedisPool(poolConfig, host, Integer.valueOf(port), Integer.valueOf(timeout));
	}
}
