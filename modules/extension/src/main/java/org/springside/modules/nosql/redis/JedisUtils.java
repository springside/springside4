package org.springside.modules.nosql.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class JedisUtils {
	public static final String DEFAULT_HOST = "localhost";
	public static final int DEFAULT_PORT = Protocol.DEFAULT_PORT;
	public static final int DEFAULT_TIMEOUT = Protocol.DEFAULT_TIMEOUT;

	private static final String OK_CODE = "OK";
	private static final String OK_MULTI_CODE = "+OK";

	/**
	 * 判断 是 OK 或 +OK.
	 */
	public static boolean isStatusOk(String status) {
		return (status != null) && (OK_CODE.equals(status) || OK_MULTI_CODE.equals(status));
	}

	/**
	 * 快速设置JedisPoolConfig, 不执行idle checking。
	 */
	public static JedisPoolConfig createPoolConfig(int maxIdle, int maxActive) {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(maxIdle);
		poolConfig.setMaxActive(maxActive);
		poolConfig.setTimeBetweenEvictionRunsMillis(-1);
		return poolConfig;
	}

	/**
	 * 快速设置JedisPoolConfig, 设置执行idle checking的间隔和可清除的idle时间.
	 */
	public static JedisPoolConfig createPoolConfig(int maxIdle, int maxActive, int checkingIntervalSecs,
			int evictableIdleTimeSecs) {
		JedisPoolConfig poolConfig = createPoolConfig(maxIdle, maxActive);
		poolConfig.setTimeBetweenEvictionRunsMillis(checkingIntervalSecs * 1000);
		poolConfig.setMinEvictableIdleTimeMillis(evictableIdleTimeSecs * 1000);
		return poolConfig;
	}

	/**
	 * 退出然后关闭Jedis连接。
	 */
	public static void closeJedis(Jedis jedis) {
		if (jedis.isConnected()) {
			try {
				try {
					jedis.quit();
				} catch (Exception e) {
				}
				jedis.disconnect();
			} catch (Exception e) {

			}
		}
	}
}
