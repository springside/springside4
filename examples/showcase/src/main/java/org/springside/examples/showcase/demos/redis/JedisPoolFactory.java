package org.springside.examples.showcase.demos.redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolFactory {

	public static JedisPool createJedisPool(String defaultHost, int defaultPort, int defaultTimeout, int poolSize) {
		// merge default setting and system properties
		String host = System.getProperty("benchmark.host", defaultHost);
		String port = System.getProperty("benchmark.port", String.valueOf(defaultPort));
		String timeout = System.getProperty("benchmark.timeout", String.valueOf(defaultTimeout));

		// 设置Pool
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxActive(poolSize);
		poolConfig.setMaxIdle(poolSize);

		// create jedis pool
		return new JedisPool(poolConfig, host, Integer.valueOf(port), Integer.valueOf(timeout));
	}
}
