package org.springside.modules.nosql.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

/**
 * JedisTemplate is the template to execute jedis actions, handle the connection with the pool correctly.
 */
public class JedisTemplate {
	private static Logger logger = LoggerFactory.getLogger(JedisTemplate.class);

	private JedisPool jedisPool;

	public JedisTemplate(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	/**
	 * Execute with a call back action with result.
	 */
	public <T> T execute(JedisAction<T> jedisAction) throws JedisException {
		Jedis jedis = null;
		boolean broken = false;
		try {
			jedis = jedisPool.getResource();
			return jedisAction.action(jedis);
		} catch (JedisConnectionException e) {
			logger.error("Redis connection lost.", e);
			broken = true;
			throw e;
		} finally {
			closeResource(jedis, broken);
		}
	}

	/**
	 * Execute with a call back action without result.
	 */
	public void execute(JedisActionNoResult jedisAction) throws JedisException {
		Jedis jedis = null;
		boolean broken = false;
		try {
			jedis = jedisPool.getResource();
			jedisAction.action(jedis);
		} catch (JedisConnectionException e) {
			logger.error("Redis connection lost.", e);
			broken = true;
			throw e;
		} finally {
			closeResource(jedis, broken);
		}
	}

	/**
	 * Return jedis connection to the pool, call different return methods depends on the conectionBroken status.
	 */
	protected void closeResource(Jedis jedis, boolean connectionBroken) {
		if (jedis != null) {
			if (connectionBroken) {
				jedisPool.returnBrokenResource(jedis);
			} else {
				jedisPool.returnResource(jedis);
			}
		}
	}

	/**
	 * Get the internal JedisPool.
	 */
	public JedisPool getJedisPool() {
		return jedisPool;
	}

	/**
	 * Callback interface for template method.
	 */
	public interface JedisAction<T> {
		T action(Jedis jedis);
	}

	/**
	 * Callback interface for template method without result.
	 */
	public interface JedisActionNoResult {
		void action(Jedis jedis);
	}

	// ////////////// 常用方法的封装 ///////////////////////// //

	// ////////////// Key ///////////////////////// //
	public Long del(final String key) {
		return execute(new JedisAction<Long>() {

			@Override
			public Long action(Jedis jedis) {
				return jedis.del(key);
			}
		});
	}

	public void flushDB() {
		execute(new JedisActionNoResult() {

			@Override
			public void action(Jedis jedis) {
				jedis.flushDB();
			}
		});
	}

	// ////////////// String ///////////////////////// //
	public <T> T get(final String key) {
		return execute(new JedisAction<T>() {

			@Override
			public T action(Jedis jedis) {
				return (T) jedis.get(key);
			}
		});
	}

	public void set(final String key, final String value) {
		execute(new JedisActionNoResult() {

			@Override
			public void action(Jedis jedis) {
				jedis.set(key, value);
			}
		});
	}

	public Long incr(final String key) {
		return execute(new JedisAction<Long>() {

			@Override
			public Long action(Jedis jedis) {
				return jedis.incr(key);
			}
		});
	}

	public Long decr(final String key) {
		return execute(new JedisAction<Long>() {

			@Override
			public Long action(Jedis jedis) {
				return jedis.decr(key);
			}
		});
	}

	// ////////////// List ///////////////////////// //
	public void lpush(final String key, final String value) {
		execute(new JedisActionNoResult() {

			@Override
			public void action(Jedis jedis) {
				jedis.lpush(key, value);
			}
		});
	}

	// ////////////// Sorted Set ///////////////////////// //
	public void zadd(final String key, final double score, final String value) {
		execute(new JedisActionNoResult() {

			@Override
			public void action(Jedis jedis) {
				jedis.zadd(key, score, value);
			}
		});
	}
}
