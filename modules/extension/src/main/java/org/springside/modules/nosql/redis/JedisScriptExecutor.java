package org.springside.modules.nosql.redis;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.nosql.redis.JedisTemplate.JedisAction;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisDataException;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Load and Run the lua scripts and support to reload the script when execution failed.
 */
public class JedisScriptExecutor {
	private static Logger logger = LoggerFactory.getLogger(JedisScriptExecutor.class);

	private JedisTemplate jedisTemplate;

	// Map contains <Script Hash, Script Content> pair
	private BiMap<String, String> hashScriptMap;
	// Map contains <Script Content, Script Hash> pair
	private BiMap<String, String> scriptHashMap;

	public JedisScriptExecutor(JedisPool jedisPool) {
		this.jedisTemplate = new JedisTemplate(jedisPool);
		hashScriptMap = HashBiMap.create();
		scriptHashMap = hashScriptMap.inverse();
	}

	/**
	 * Load the script to redis, return the script hash.
	 */
	public synchronized String load(final String script) {
		String hash = scriptHashMap.get(script);

		if (hash == null) {
			hash = jedisTemplate.execute(new JedisTemplate.JedisAction<String>() {
				@Override
				public String action(Jedis jedis) {
					return jedis.scriptLoad(script);
				}
			});
			hashScriptMap.put(hash, script);
		}

		return hash;
	}

	/**
	 * Execute the script, auto reload the script if it is not in redis.
	 */
	public Object execute(final String hash, final String[] keys, final String[] args) {
		return execute(hash, Arrays.asList(keys), Arrays.asList(args));
	}

	/**
	 * Execute the script, auto reload the script if it is not in redis.
	 */
	public Object execute(final String hash, final List<String> keys, final List<String> args) {
		if (!hashScriptMap.containsKey(hash)) {
			throw new IllegalArgumentException("Script hash " + hash + " is not loaded in executor");
		}

		try {
			return jedisTemplate.execute(new JedisAction<Object>() {
				@Override
				public Object action(Jedis jedis) {
					return jedis.evalsha(hash, keys, args);
				}
			});
		} catch (JedisDataException e) {
			logger.warn("Lua execution error, try to reload the script.", e);
			return reloadAndExecute(hash, keys, args);
		}
	}

	/**
	 * Reload the script and execute it again.
	 */
	private Object reloadAndExecute(final String hash, final List<String> keys, final List<String> args) {

		return jedisTemplate.execute(new JedisAction<Object>() {
			@Override
			public Object action(Jedis jedis) {
				// concurrent checking again
				if (!jedis.scriptExists(hash)) {
					String script = hashScriptMap.get(hash);
					jedis.scriptLoad(script);
				}

				return jedis.evalsha(hash, keys, args);
			}
		});
	}

	// just for test.
	public Map<String, String> getHashScriptMap() {
		return hashScriptMap;
	}
}
