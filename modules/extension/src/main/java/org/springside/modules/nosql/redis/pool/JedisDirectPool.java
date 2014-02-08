package org.springside.modules.nosql.redis.pool;

import redis.clients.jedis.JedisPoolConfig;

/**
 * Pool which connect to redis instance directly.
 */
public class JedisDirectPool extends JedisPool {

	public JedisDirectPool(ConnectionInfo connectionInfo, JedisPoolConfig config) {
		initRedisPool(connectionInfo, config);
	}
}
