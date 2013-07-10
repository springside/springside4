package org.springside.examples.showcase.demos.redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolFactory {

	public static JedisPool createJedisPool(String defaultHost, int defaultPort, int defaultTimeout, int threadCount) {
		// 合并命令行传入的系统变量与默认值
		String host = System.getProperty("benchmark.host", defaultHost);
		String port = System.getProperty("benchmark.port", String.valueOf(defaultPort));
		String timeout = System.getProperty("benchmark.timeout", String.valueOf(defaultTimeout));

		// 设置Pool大小，设为与线程数等大。
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxActive(threadCount);
		poolConfig.setMaxIdle(threadCount);

		// create jedis pool
		return new JedisPool(poolConfig, host, Integer.valueOf(port), Integer.valueOf(timeout));
	}
}
