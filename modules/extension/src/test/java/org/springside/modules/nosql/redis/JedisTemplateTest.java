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
		assertThat((int) jedisTemplate.getAsInt(key)).isEqualTo(123);
		assertThat((int) jedisTemplate.getAsInt(notExistKey)).isZero();

		jedisTemplate.set(key, value);
		assertThat((long) jedisTemplate.getAsLong(key)).isEqualTo(123L);
		assertThat((long) jedisTemplate.getAsLong(notExistKey)).isZero();
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
}
