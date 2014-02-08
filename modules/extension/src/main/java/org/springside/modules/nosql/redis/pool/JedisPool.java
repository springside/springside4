package org.springside.modules.nosql.redis.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.Pool;

/**
 * Jedis Pool base class.
 */
public abstract class JedisPool extends Pool<Jedis> {

	protected ConnectionInfo connectionInfo;

	/**
	 * Create a JedisPoolConfig with new maxPoolSize, JedisPoolConfig's default maxPoolSize is only 8. And also reset
	 * the idle checking time.
	 */
	public static JedisPoolConfig createPoolConfig(int maxPoolSize) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxPoolSize);

		// reset the idle connection checking time from 30 seconds to 1hour
		config.setTimeBetweenEvictionRunsMillis(3600 * 1000);

		return config;
	}

	/**
	 * Initialize the internal pool with connection info and pool config.
	 */
	protected void initRedisPool(ConnectionInfo connectionInfo, JedisPoolConfig config) {
		this.connectionInfo = connectionInfo;
		JedisFactory factory = new JedisFactory(connectionInfo.getHost(), connectionInfo.getPort(),
				connectionInfo.getTimeout(), connectionInfo.getPassword(), connectionInfo.getDatabase());

		internalPool = new GenericObjectPool(factory, config);
	}

	/**
	 * Destroy the internal pool.
	 */
	protected void destroyRedisPool() {
		super.destroy();
	}

	/**
	 * Return a broken jedis connection back to pool.
	 */
	@Override
	public void returnBrokenResource(final Jedis resource) {
		returnBrokenResourceObject(resource);
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
