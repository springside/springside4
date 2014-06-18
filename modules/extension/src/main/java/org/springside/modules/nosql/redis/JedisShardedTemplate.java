/*------------------------------------------------------------------------------
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
package org.springside.modules.nosql.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springside.modules.nosql.redis.JedisTemplate.JedisAction;
import org.springside.modules.nosql.redis.JedisTemplate.JedisActionNoResult;
import org.springside.modules.nosql.redis.pool.JedisPool;

import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.Hashing;

/**
 * JedisShardTemplate, 在JedisTemplate的基础上支持按key进行sharding分区，
 * 如果只传入一个JedisPool，则会退化到JedisTemplate, 不会进行sharding运算。
 */
public class JedisShardedTemplate {

	private final Hashing algo = Hashing.MURMUR_HASH;
	private TreeMap<Long, JedisTemplate> nodes = new TreeMap<Long, JedisTemplate>();
	private JedisTemplate singleTemplate = null;

	public JedisShardedTemplate(JedisPool[] jedisPools) {
		if (jedisPools.length == 1) {
			singleTemplate = new JedisTemplate(jedisPools[0]);
		} else {
			initNodes(jedisPools);
		}
	}

	private void initNodes(JedisPool... jedisPools) {
		for (int i = 0; i != jedisPools.length; i++) {
			// 环上更多的点让负载更均衡
			for (int n = 0; n < 5; n++) {
				final JedisPool jedisPool = jedisPools[i];
				nodes.put(this.algo.hash("SHARD-" + i + "-NODE-" + n), new JedisTemplate(jedisPool));
			}
		}
	}

	/**
	 * 对Key进行哈希并返回哈希环上对应的节点。
	 * 参考了Jedis的Sharded.java
	 */
	public JedisTemplate getShard(String key) {

		if (singleTemplate != null) {
			return singleTemplate;
		}

		SortedMap<Long, JedisTemplate> tail = nodes.tailMap(algo.hash(key));
		// 已到最后，返回第一个点.
		if (tail.isEmpty()) {
			return nodes.get(nodes.firstKey());
		}

		return tail.get(tail.firstKey());
	}

	/**
	 * 包含key以帮助sharding的execute 版本.
	 */
	public <T> T execute(String key, JedisAction<T> jedisAction) throws JedisException {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.execute(jedisAction);
	}

	/**
	 * 包含key以帮助sharding的execute 版本.
	 */
	public void execute(String key, JedisActionNoResult jedisAction) throws JedisException {
		JedisTemplate jedisTemplate = getShard(key);
		jedisTemplate.execute(jedisAction);
	}

	// / Common Actions ///

	public Boolean del(final String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.del(key);
	}

	// / String Actions ///

	public String get(final String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.get(key);
	}

	public void set(final String key, final String value) {
		JedisTemplate jedisTemplate = getShard(key);
		jedisTemplate.set(key, value);
	}

	public void setex(String key, String value, int seconds) {
		JedisTemplate jedisTemplate = getShard(key);
		jedisTemplate.setex(key, value, seconds);
	}

	public Boolean setnx(String key, String value) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.setnx(key, value);
	}

	public Boolean setnxex(String key, String value, int seconds) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.setnxex(key, value, seconds);
	}

	public Long incr(String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.incr(key);
	}

	public Long decr(String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.decr(key);
	}

	// / Hash Actions ///

	public String hget(final String key, final String field) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.hget(key, field);
	}

	public void hset(final String key, final String field, final String value) {
		JedisTemplate jedisTemplate = getShard(key);
		jedisTemplate.hset(key, field, value);
	}

	public List<String> hmget(String key, String[] fields) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.hmget(key, fields);
	}

	public void hmset(String key, Map<String, String> map) {
		JedisTemplate jedisTemplate = getShard(key);
		jedisTemplate.hmset(key, map);
	}

	public Long hdel(final String key, final String... fieldsName) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.hdel(key, fieldsName);
	}

	public Set<String> hkeys(final String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.hkeys(key);
	}

	// ////////////// 关于List ///////////////////////////
	public void lpush(final String key, final String... values) {
		JedisTemplate jedisTemplate = getShard(key);
		jedisTemplate.lpush(key, values);
	}

	public String rpop(final String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.rpop(key);
	}

	/**
	 * 返回List长度, key不存在时返回0，key类型不是list时抛出异常.
	 */
	public Long llen(final String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.llen(key);
	}

	/**
	 * 删除List中的第一个等于value的元素，value不存在或key不存在时返回false.
	 */
	public Boolean lremOne(final String key, final String value) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.lremOne(key, value);
	}

	/**
	 * 删除List中的所有等于value的元素，value不存在或key不存在时返回false.
	 */
	public Boolean lremAll(final String key, final String value) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.lremAll(key, value);
	}

	// ////////////// 关于Sorted Set ///////////////////////////
	/**
	 * 加入Sorted set, 如果member在Set里已存在, 只更新score并返回false, 否则返回true.
	 */
	public Boolean zadd(final String key, final String member, final double score) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.zadd(key, member, score);
	}

	/**
	 * 删除sorted set中的元素，成功删除返回true，key或member不存在返回false。
	 */
	public Boolean zrem(final String key, final String member) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.zrem(key, member);
	}

	/**
	 * 当key不存在时返回null.
	 */
	public Double zscore(final String key, final String member) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.zscore(key, member);
	}

	/**
	 * 返回sorted set长度, key不存在时返回0.
	 */
	public Long zcard(final String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.zcard(key);
	}
}
