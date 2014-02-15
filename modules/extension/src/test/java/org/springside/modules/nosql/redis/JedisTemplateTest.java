/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.nosql.redis;

import static org.assertj.core.api.Assertions.*;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.lordofthejars.nosqlunit.redis.EmbeddedRedis;
import com.lordofthejars.nosqlunit.redis.EmbeddedRedis.EmbeddedRedisRuleBuilder;
import com.lordofthejars.nosqlunit.redis.EmbeddedRedisInstances;

public class JedisTemplateTest {

	private JedisTemplate jedisTemplate;

	@ClassRule
	public static EmbeddedRedis embeddedRedisRule = EmbeddedRedisRuleBuilder.newEmbeddedRedisRule().build();

	@Before
	public void setup() {
		Jedis embeddedRedis = EmbeddedRedisInstances.getInstance().getDefaultJedis();
		JedisPool jedisPool = Mockito.mock(JedisPool.class);
		Mockito.when(jedisPool.getResource()).thenReturn(embeddedRedis);

		jedisTemplate = new JedisTemplate(jedisPool);
	}

	@Test
	public void stringActions() {
		String key = "test.string.key";
		String notExistKey = key + "not.exist";
		String value = "123";

		jedisTemplate.set(key, value);
		assertThat(jedisTemplate.get(key)).isEqualTo(value);
		assertThat(jedisTemplate.get(notExistKey)).isNull();

		assertThat(jedisTemplate.del(key)).isTrue();
		assertThat(jedisTemplate.del(notExistKey)).isFalse();

		jedisTemplate.set(key, value);
		assertThat(jedisTemplate.getAsInt(key)).isEqualTo(123);
		assertThat(jedisTemplate.getAsInt(notExistKey)).isNull();

		jedisTemplate.set(key, value);
		assertThat(jedisTemplate.getAsLong(key)).isEqualTo(123L);
		assertThat(jedisTemplate.getAsLong(notExistKey)).isNull();

		assertThat(jedisTemplate.setnx(key, value)).isFalse();
		assertThat(jedisTemplate.setnx(key + "nx", value)).isTrue();
	}

	@Test
	public void listActions() {
		String key = "test.list.key";
		String value = "123";
		String value2 = "456";

		// push/pop single element
		jedisTemplate.lpush(key, value);
		assertThat(jedisTemplate.llen(key)).isEqualTo(1);
		assertThat(jedisTemplate.rpop(key)).isEqualTo(value);
		assertThat(jedisTemplate.rpop(key)).isNull();

		// push/pop two elements
		jedisTemplate.lpush(key, value);
		jedisTemplate.lpush(key, value2);
		assertThat(jedisTemplate.llen(key)).isEqualTo(2);
		assertThat(jedisTemplate.rpop(key)).isEqualTo(value);
		assertThat(jedisTemplate.rpop(key)).isEqualTo(value2);

		// remove elements
		jedisTemplate.lpush(key, value);
		jedisTemplate.lpush(key, value);
		jedisTemplate.lpush(key, value);
		assertThat(jedisTemplate.llen(key)).isEqualTo(3);
		assertThat(jedisTemplate.lremOne(key, value)).isTrue();
		assertThat(jedisTemplate.llen(key)).isEqualTo(2);
		assertThat(jedisTemplate.lremAll(key, value)).isTrue();
		assertThat(jedisTemplate.llen(key)).isEqualTo(0);
		assertThat(jedisTemplate.lremAll(key, value)).isFalse();
	}

	@Test
	public void orderedSetActions() {
		String key = "test.orderedSet.key";
		String member = "abc";
		String member2 = "def";
		double score1 = 1;
		double score11 = 11;
		double score2 = 2;

		// zadd
		assertThat(jedisTemplate.zadd(key, member, score1)).isTrue();
		assertThat(jedisTemplate.zadd(key, member2, score2)).isTrue();

		// zcard
		assertThat(jedisTemplate.zcard(key)).isEqualTo(2);
		assertThat(jedisTemplate.zcard(key + "not.exist")).isEqualTo(0);

		// zrem
		assertThat(jedisTemplate.zrem(key, member2)).isTrue();
		assertThat(jedisTemplate.zcard(key)).isEqualTo(1);
		assertThat(jedisTemplate.zrem(key, member2 + "not.exist")).isFalse();

		// unique & zscore
		assertThat(jedisTemplate.zadd(key, member, score11)).isFalse();
		assertThat(jedisTemplate.zcard(key)).isEqualTo(1);
		assertThat(jedisTemplate.zscore(key, member)).isEqualTo(score11);
		assertThat(jedisTemplate.zscore(key, member + "not.exist")).isNull();
	}
}
