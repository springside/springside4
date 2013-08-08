package org.springside.modules.nosql.redis;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.nosql.redis.JedisTemplate.JedisAction;
import org.springside.modules.utils.StopWatch;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisDataException;

import com.google.common.collect.Maps;

/**
 * 装载并执行Lua Script，如果服务器上因为集群多台服务器或重启等原因没有装载script，会自动重新装载后重试。
 */
public class JedisScriptExecutor {
	private static Logger logger = LoggerFactory.getLogger(JedisScriptExecutor.class);

	private JedisTemplate jedisTemplate;

	// 以 <Script Sha1, Script Content>存储装载过的script，用于重试。
	private Map<String, String> scriptMap = Maps.newHashMap();

	public JedisScriptExecutor(JedisPool jedisPool) {
		this.jedisTemplate = new JedisTemplate(jedisPool);
	}

	/**
	 * 装载Lua Script，返回Sha1 Hash值。
	 * 如果Script出错，抛出JedisDataException。
	 */
	public synchronized String load(final String script) throws JedisDataException {
		String sha1 = jedisTemplate.execute(new JedisTemplate.JedisAction<String>() {
			@Override
			public String action(Jedis jedis) {
				return jedis.scriptLoad(script);
			}
		});

		logger.info("Script \"{}\" had been loaded as {}", script, sha1);
		scriptMap.put(sha1, script);
		return sha1;
	}

	/**
	 * 执行Lua Script, 如果Redis服务器上还没装载Script则自动装载并重试。
	 * 如果Script之前未在executor内装载，抛出IllegalArgumentException。
	 */
	public Object execute(final String sha1, final String[] keys, final String[] args) throws IllegalArgumentException {
		return execute(sha1, Arrays.asList(keys), Arrays.asList(args));
	}

	/**
	 * 执行Lua Script, 如果Redis服务器上还没装载Script则自动装载并重试。
	 * 如果Script之前未在executor内装载，抛出IllegalArgumentException。
	 */
	public Object execute(final String sha1, final List<String> keys, final List<String> args)
			throws IllegalArgumentException {
		if (!scriptMap.containsKey(sha1)) {
			throw new IllegalArgumentException("Script sha1 " + sha1 + " is not loaded in executor。");
		}

		return jedisTemplate.execute(new JedisAction<Object>() {
			@Override
			public Object action(Jedis jedis) {
				StopWatch stopWatch = new StopWatch();

				try {
					return jedis.evalsha(sha1, keys, args);
				} catch (JedisDataException e) {
					logger.warn("Script sha1 {} is not loaded in server yet, try to reload and run it again", sha1, e);
					stopWatch.reset();
					String script = scriptMap.get(sha1);
					return jedis.eval(script, keys, args);
				} finally {
					logger.debug("Script sha1 {} execution time is {}ms", sha1, stopWatch.getMillis());
				}
			}
		});
	}
}
