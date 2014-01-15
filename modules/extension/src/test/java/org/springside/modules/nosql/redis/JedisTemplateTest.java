package org.springside.modules.nosql.redis;

import static org.junit.Assert.*;

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
		assertEquals(jedisTemplate.get(key), value);
		assertNull(jedisTemplate.get(notExistKey));

		assertTrue(jedisTemplate.del(key));
		assertFalse(jedisTemplate.del(notExistKey));

		jedisTemplate.set(key, value);
		assertEquals((int) jedisTemplate.getAsInt(key), 123);
		assertEquals((int) jedisTemplate.getAsInt(notExistKey), 0);

		jedisTemplate.set(key, value);
		assertEquals((long) jedisTemplate.getAsLong(key), 123L);
		assertEquals((long) jedisTemplate.getAsLong(notExistKey), 0);
	}
}
