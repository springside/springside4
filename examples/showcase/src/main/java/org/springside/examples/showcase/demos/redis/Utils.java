package org.springside.examples.showcase.demos.redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Utils {

	public static JedisPool createJedisPool(String defaultHost, int defaultPort, int defaultTimeout, int threadCount) {
		//merge default setting and system properties
		String host = System.getProperty("benchmark.host", defaultHost);
		int port = new Integer(System.getProperty("benchmark.port", String.valueOf(defaultPort)));
		int timeout = new Integer(System.getProperty("benchmark.timeout", String.valueOf(defaultPort)));

		//create jedis pool
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxActive(threadCount);
		return new JedisPool(poolConfig, host, port, timeout);
	}
}
