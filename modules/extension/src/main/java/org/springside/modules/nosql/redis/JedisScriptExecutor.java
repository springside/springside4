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

	// 以 <Script Hash, Script Content>存储装载过的script，用于重试。
	private Map<String, String> hashScriptMap = Maps.newHashMap();

	public JedisScriptExecutor(JedisPool jedisPool) {
		this.jedisTemplate = new JedisTemplate(jedisPool);
	}

	/**
	 * 装载Lua Script，返回Hash值。
	 * 如果Script出错，抛出JedisDataException。
	 */
	public synchronized String load(final String script) throws JedisDataException {
		String hash = jedisTemplate.execute(new JedisTemplate.JedisAction<String>() {
			@Override
			public String action(Jedis jedis) {
				return jedis.scriptLoad(script);
			}
		});
		hashScriptMap.put(hash, script);
		return hash;
	}

	/**
	 * 执行Lua Script, 如果Redis服务器上还没装载Script则自动装载并重试。
	 * 如果Script之前未在executor内装载，抛出IllegalArgumentException。
	 */
	public Object execute(final String hash, final String[] keys, final String[] args) throws IllegalArgumentException {
		return execute(hash, Arrays.asList(keys), Arrays.asList(args));
	}

	/**
	 * 执行Lua Script, 如果Redis服务器上还没装载Script则自动装载并重试。
	 * 如果Script之前未在executor内装载，抛出IllegalArgumentException。
	 */
	public Object execute(final String hash, final List<String> keys, final List<String> args)
			throws IllegalArgumentException {
		if (!hashScriptMap.containsKey(hash)) {
			throw new IllegalArgumentException("Script hash " + hash + " is not loaded in executor。");
		}

		final StopWatch stopWatch = new StopWatch();

		try {
			return jedisTemplate.execute(new JedisAction<Object>() {
				@Override
				public Object action(Jedis jedis) {
					Object result = jedis.evalsha(hash, keys, args);
					logger.debug("Script hash {} execution time is {}ms", hash, stopWatch.getMillis());
					return result;
				}
			});
		} catch (JedisDataException e) {
			logger.warn("Script hash {} is not loaded yet, try to reload and run it again", hash, e);
			stopWatch.reset();
			Object result = reloadAndExecute(hash, keys, args);
			logger.debug("Script hash {} reload and execution time is {}ms", hash, stopWatch.getMillis());
			return result;
		}
	}

	/**
	 * 重新装载script并执行。
	 */
	private Object reloadAndExecute(final String hash, final List<String> keys, final List<String> args) {

		return jedisTemplate.execute(new JedisAction<Object>() {
			@Override
			public Object action(Jedis jedis) {
				String script = hashScriptMap.get(hash);
				jedis.scriptLoad(script);
				return jedis.evalsha(hash, keys, args);
			}
		});
	}
}
