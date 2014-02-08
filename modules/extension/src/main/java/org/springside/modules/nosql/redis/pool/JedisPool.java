package org.springside.modules.nosql.redis.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.Pool;

/**
 * Pool which connect to redis instance directly.
 */
public class JedisPool extends Pool<Jedis> {

	private ConnectionInfo connectionInfo;

	public JedisPool(ConnectionInfo connectionInfo, JedisPoolConfig config) {
		this.connectionInfo = connectionInfo;

		JedisFactory factory = new JedisFactory(connectionInfo.getHost(), connectionInfo.getPort(),
				connectionInfo.getTimeout(), connectionInfo.getPassword(), connectionInfo.getDatabase(),
				connectionInfo.getClientName());

		internalPool = new GenericObjectPool(factory, config);
	}

	/**
	 * Return a available jedis connection back to pool.
	 */
	@Override
	public void returnResource(final Jedis resource) {
		resource.resetState();
		returnResourceObject(resource);
	}

	public ConnectionInfo getConnectionInfo() {
		return connectionInfo;
	}
}
