/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.nosql.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springside.modules.nosql.redis.JedisTemplate.JedisAction;
import org.springside.modules.nosql.redis.JedisTemplate.JedisActionNoResult;
import org.springside.modules.nosql.redis.JedisTemplate.PipelineAction;
import org.springside.modules.nosql.redis.JedisTemplate.PipelineActionNoResult;
import org.springside.modules.nosql.redis.pool.JedisPool;

import redis.clients.jedis.Tuple;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.Hashing;

/**
 * JedisShardTemplate is the JedisTemplate, which has key sharding feature.
 * 
 * Pass more than one JedisPool to the it, it will calculate which jedisPool will handle the key. If
 * only one jedisPool passed, it won't do the calculation, so please use JedisShardedTemplate by default.
 * 
 * But it doesn't support multi-key actions by default, like:
 * 1. Pipelined and Transaction process multi-keys
 * 2. Methods in @redis.clients.jedis.MultiKeyCommands like mget, rpoplpush....
 * 
 * To support them, please use a common sharding key relate to these keys when invoke the APIs.
 */
public class JedisShardedTemplate {

	private final Hashing algo = Hashing.MURMUR_HASH;
	private TreeMap<Long, JedisTemplate> nodes = new TreeMap<Long, JedisTemplate>();
	private JedisTemplate singleTemplate = null;

	public JedisShardedTemplate(JedisPool... jedisPools) {
		if (jedisPools.length == 1) {
			singleTemplate = new JedisTemplate(jedisPools[0]);
		} else {
			initNodes(jedisPools);
		}
	}

	public JedisShardedTemplate(List<JedisPool> jedisPools) {
		this(jedisPools.toArray(new JedisPool[jedisPools.size()]));
	}

	private void initNodes(JedisPool... jedisPools) {
		for (int i = 0; i != jedisPools.length; i++) {
			// more entry the make the hash ring be more balance
			for (int n = 0; n < 128; n++) {
				final JedisPool jedisPool = jedisPools[i];
				nodes.put(this.algo.hash("SHARD-" + i + "-NODE-" + n), new JedisTemplate(jedisPool));
			}
		}
	}

	/**
	 * Hash the key and get the jedisTemplate from the hash ring. Get idea from Jedis's Sharded.java
	 */
	public JedisTemplate getShard(String key) {
		if (singleTemplate != null) {
			return singleTemplate;
		}

		SortedMap<Long, JedisTemplate> tail = nodes.tailMap(algo.hash(key));
		if (tail.isEmpty()) {
			// the last node, back to first.
			return nodes.get(nodes.firstKey());
		}
		return tail.get(tail.firstKey());
	}

	/*
	 * Execute the action.
	 * 
	 * @key the action must process only this key, or this key is a sharding key.
	 */
	public <T> T execute(String key, JedisAction<T> jedisAction) throws JedisException {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.execute(jedisAction);
	}

	/*
	 * Execute the action.
	 * 
	 * @key the action must process only this key, or this key is a sharding key.
	 */
	public void execute(String key, JedisActionNoResult jedisAction) throws JedisException {
		JedisTemplate jedisTemplate = getShard(key);
		jedisTemplate.execute(jedisAction);
	}

	/*
	 * Execute the pipeline.
	 * 
	 * @key the action must process only this key, or this key is a sharding key.
	 */
	public List<Object> execute(String key, PipelineAction pipelineAction) throws JedisException {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.execute(pipelineAction);
	}

	/*
	 * Execute the pipeline.
	 * 
	 * @key the action must process only this key, or this key is a sharding key.
	 */
	public void execute(String key, PipelineActionNoResult pipelineAction) throws JedisException {
		JedisTemplate jedisTemplate = getShard(key);
		jedisTemplate.execute(pipelineAction);
	}

	// / Common Actions ///

	public Boolean del(final String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.del(key);
	}

	public Boolean del(final String shardingKey, final String key) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.del(key);
	}

	// / String Actions ///

	public String get(final String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.get(key);
	}

	public String get(final String shardingKey, final String key) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.get(key);
	}

	public Long getAsLong(final String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.getAsLong(key);
	}

	public Long getAsLong(final String shardingKey, final String key) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.getAsLong(key);
	}

	public Integer getAsInt(final String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.getAsInt(key);
	}

	public Integer getAsInt(final String shardingKey, final String key) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.getAsInt(key);
	}

	public void set(final String key, final String value) {
		JedisTemplate jedisTemplate = getShard(key);
		jedisTemplate.set(key, value);
	}

	public void set(final String shardingKey, final String key, final String value) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		jedisTemplate.set(key, value);
	}

	public void setex(final String key, final String value, final int seconds) {
		JedisTemplate jedisTemplate = getShard(key);
		jedisTemplate.setex(key, value, seconds);
	}

	public void setex(final String shardingKey, final String key, final String value, final int seconds) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		jedisTemplate.setex(key, value, seconds);
	}

	public Boolean setnx(final String key, final String value) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.setnx(key, value);
	}

	public Boolean setnx(final String shardingKey, final String key, final String value) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.setnx(key, value);
	}

	public Boolean setnxex(final String key, final String value, final int seconds) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.setnxex(key, value, seconds);
	}

	public Boolean setnxex(final String shardingKey, final String key, final String value, final int seconds) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.setnxex(key, value, seconds);
	}

	public String getSet(final String key, final String value) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.getSet(key, value);
	}

	public String getSet(final String shardingKey, final String key, final String value) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.getSet(key, value);
	}

	public Long incr(final String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.incr(key);
	}

	public Long incr(final String shardingKey, final String key) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.incr(key);
	}

	public Long incrBy(final String key, final Long increment) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.incrBy(key, increment);
	}

	public Long incrBy(final String shardingKey, final String key, final Long increment) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.incrBy(key, increment);
	}

	public Double incrByFloat(final String key, final double increment) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.incrByFloat(key, increment);
	}

	public Double incrByFloat(final String shardingKey, final String key, final double increment) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.incrByFloat(key, increment);
	}

	public Long decr(final String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.decr(key);
	}

	public Long decr(final String shardingKey, final String key) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.decr(key);
	}

	public Long decrBy(final String key, final Long decrement) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.decrBy(key, decrement);
	}

	public Long decrBy(final String shardingKey, final String key, final Long decrement) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.decrBy(key, decrement);
	}

	// / Hash Actions ///

	public String hget(final String key, final String field) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.hget(key, field);
	}

	public String hget(final String shardingKey, final String key, final String field) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.hget(key, field);
	}

	public List<String> hmget(final String key, final String field) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.hmget(key, field);
	}

	public List<String> hmget(final String key, final String[] fields) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.hmget(key, fields);
	}

	public List<String> hmget(final String shardingKey, final String key, final String field) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.hmget(key, field);
	}

	public List<String> hmget(final String shardingKey, final String key, final String[] fields) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.hmget(key, fields);
	}

	public Map<String, String> hgetAll(final String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.hgetAll(key);
	}

	public Map<String, String> hgetAll(final String shardingKey, final String key) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.hgetAll(key);
	}

	public void hset(final String key, final String field, final String value) {
		JedisTemplate jedisTemplate = getShard(key);
		jedisTemplate.hset(key, field, value);
	}

	public void hset(final String shardingKey, final String key, final String field, final String value) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		jedisTemplate.hset(key, field, value);
	}

	public void hmset(final String key, final Map<String, String> map) {
		JedisTemplate jedisTemplate = getShard(key);
		jedisTemplate.hmset(key, map);
	}

	public void hmset(final String shardingKey, final String key, final Map<String, String> map) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		jedisTemplate.hmset(key, map);
	}

	public Boolean hsetnx(final String key, final String fieldName, final String value) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.hsetnx(key, fieldName, value);
	}

	public Boolean hsetnx(final String shardingKey, final String key, final String fieldName, final String value) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.hsetnx(key, fieldName, value);
	}

	public Long hincrBy(final String key, final String fieldName, final long increment) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.hincrBy(key, fieldName, increment);
	}

	public Long hincrBy(final String shardingKey, final String key, final String fieldName, final long increment) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.hincrBy(key, fieldName, increment);
	}

	public Double hincrByFloat(final String key, final String fieldName, final double increment) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.hincrByFloat(key, fieldName, increment);
	}

	public Double hincrByFloat(final String shardingKey, final String key, final String fieldName,
			final double increment) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.hincrByFloat(key, fieldName, increment);
	}

	public Long hdel(final String key, final String fieldsName) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.hdel(key, fieldsName);
	}

	public Long hdel(final String key, final String[] fieldsNames) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.hdel(key, fieldsNames);
	}

	public Long hdel(final String shardingKey, final String key, final String fieldsName) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.hdel(key, fieldsName);
	}

	public Long hdel(final String shardingKey, final String key, final String[] fieldsNames) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.hdel(key, fieldsNames);
	}

	public Boolean hexists(final String key, final String fieldName) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.hexists(key, fieldName);
	}

	public Boolean hexists(final String shardingKey, final String key, final String fieldName) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.hexists(key, fieldName);
	}

	public Set<String> hkeys(final String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.hkeys(key);
	}

	public Set<String> hkeys(final String shardingKey, final String key) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.hkeys(key);
	}

	public Long hlen(final String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.hlen(key);
	}

	public Long hlen(final String shardingKey, final String key) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.hlen(key);
	}

	// / List Actions ///

	public Long lpush(final String key, final String value) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.lpush(key, value);
	}

	public Long lpush(final String key, final String[] values) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.lpush(key, values);
	}

	public Long lpush(final String shardingKey, final String key, final String value) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.lpush(key, value);
	}

	public Long lpush(final String shardingKey, final String key, final String[] values) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.lpush(key, values);
	}

	public String rpop(final String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.rpop(key);
	}

	public String rpop(final String shardingKey, final String key) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.rpop(key);
	}

	public String brpop(final String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.brpop(key);
	}

	public String brpop(final String shardingKey, final String key) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.brpop(key);
	}

	public String brpop(final int timeout, final String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.brpop(timeout, key);
	}

	public String brpop(final String shardingKey, final int timeout, final String key) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.brpop(timeout, key);
	}

	public Long llen(final String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.llen(key);
	}

	public Long llen(final String shardingKey, final String key) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.llen(key);
	}

	public String lindex(final String key, final long index) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.lindex(key, index);
	}

	public String lindex(final String shardingKey, final String key, final long index) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.lindex(key, index);
	}

	public List<String> lrange(final String key, final int start, final int end) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.lrange(key, start, end);
	}

	public List<String> lrange(final String shardingKey, final String key, final int start, final int end) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.lrange(key, start, end);
	}

	public void ltrim(final String key, final int start, final int end) {
		JedisTemplate jedisTemplate = getShard(key);
		jedisTemplate.ltrim(key, start, end);
	}

	public void ltrim(final String shardingKey, final String key, final int start, final int end) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		jedisTemplate.ltrim(key, start, end);
	}

	public void ltrimFromLeft(final String key, final int size) {
		JedisTemplate jedisTemplate = getShard(key);
		jedisTemplate.ltrimFromLeft(key, size);
	}

	public void ltrimFromLeft(final String shardingKey, final String key, final int size) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		jedisTemplate.ltrimFromLeft(key, size);
	}

	public Boolean lremFirst(final String key, final String value) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.lremFirst(key, value);
	}

	public Boolean lremFirst(final String shardingKey, final String key, final String value) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.lremFirst(key, value);
	}

	public Boolean lremAll(final String key, final String value) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.lremAll(key, value);
	}

	public Boolean lremAll(final String shardingKey, final String key, final String value) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.lremAll(key, value);
	}

	// / Set Actions ///
	public Boolean sadd(final String key, final String member) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.sadd(key, member);
	}

	public Boolean sadd(final String shardingKey, final String key, final String member) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.sadd(key, member);
	}

	public Set<String> smembers(final String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.smembers(key);
	}

	public Set<String> smembers(final String shardingKey, final String key) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.smembers(key);
	}

	// / Sorted Set Actions ///

	public Boolean zadd(final String key, final double score, final String member) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.zadd(key, score, member);
	}

	public Boolean zadd(final String shardingKey, final String key, final double score, final String member) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.zadd(key, score, member);
	}

	public Double zscore(final String key, final String member) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.zscore(key, member);
	}

	public Double zscore(final String shardingKey, final String key, final String member) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.zscore(key, member);
	}

	public Long zrank(final String key, final String member) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.zrank(key, member);
	}

	public Long zrank(final String shardingKey, final String key, final String member) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.zrank(key, member);
	}

	public Long zrevrank(final String key, final String member) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.zrevrank(key, member);
	}

	public Long zrevrank(final String shardingKey, final String key, final String member) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.zrevrank(key, member);
	}

	public Long zcount(final String key, final double start, final double end) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.zcount(key, start, end);
	}

	public Long zcount(final String shardingKey, final String key, final double start, final double end) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.zcount(key, start, end);
	}

	public Set<String> zrange(final String key, final int start, final int end) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.zrange(key, start, end);
	}

	public Set<String> zrange(final String shardingKey, final String key, final int start, final int end) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.zrange(key, start, end);
	}

	public Set<Tuple> zrangeWithScores(final String key, final int start, final int end) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.zrangeWithScores(key, start, end);
	}

	public Set<Tuple> zrangeWithScores(final String shardingKey, final String key, final int start, final int end) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.zrangeWithScores(key, start, end);
	}

	public Set<String> zrevrange(final String key, final int start, final int end) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.zrevrange(key, start, end);
	}

	public Set<String> zrevrange(final String shardingKey, final String key, final int start, final int end) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.zrevrange(key, start, end);
	}

	public Set<Tuple> zrevrangeWithScores(final String key, final int start, final int end) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.zrevrangeWithScores(key, start, end);
	}

	public Set<Tuple> zrevrangeWithScores(final String shardingKey, final String key, final int start, final int end) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.zrevrangeWithScores(key, start, end);
	}

	public Set<String> zrangeByScore(final String key, final double min, final double max) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.zrangeByScore(key, min, max);
	}

	public Set<String> zrangeByScore(final String shardingKey, final String key, final double min, final double max) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.zrangeByScore(key, min, max);
	}

	public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.zrangeByScoreWithScores(key, min, max);
	}

	public Set<Tuple> zrangeByScoreWithScores(final String shardingKey, final String key, final double min,
			final double max) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.zrangeByScoreWithScores(key, min, max);
	}

	public Set<String> zrevrangeByScore(final String key, final double max, final double min) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.zrevrangeByScore(key, max, min);
	}

	public Set<String> zrevrangeByScore(final String shardingKey, final String key, final double max, final double min) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.zrevrangeByScore(key, max, min);
	}

	public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.zrevrangeByScoreWithScores(key, max, min);
	}

	public Set<Tuple> zrevrangeByScoreWithScores(final String shardingKey, final String key, final double max,
			final double min) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.zrevrangeByScoreWithScores(key, max, min);
	}

	public Boolean zrem(final String key, final String member) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.zrem(key, member);
	}

	public Boolean zrem(final String shardingKey, final String key, final String member) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.zrem(key, member);
	}

	public Long zremByScore(final String key, final double min, final double max) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.zremByScore(key, min, max);
	}

	public Long zremByScore(final String shardingKey, final String key, final double min, final double max) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.zremByScore(key, min, max);
	}

	public Long zremByRank(final String key, final long start, final long end) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.zremByRank(key, start, end);
	}

	public Long zremByRank(final String shardingKey, final String key, final long start, final long end) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.zremByRank(key, start, end);
	}

	public Long zcard(final String key) {
		JedisTemplate jedisTemplate = getShard(key);
		return jedisTemplate.zcard(key);
	}

	public Long zcard(final String shardingKey, final String key) {
		JedisTemplate jedisTemplate = getShard(shardingKey);
		return jedisTemplate.zcard(key);
	}
}
