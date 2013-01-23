package org.springside.examples.showcase.demos.redis;

import org.springside.modules.mapper.JsonMapper;
import org.springside.modules.test.benchmark.BenchmarkTask;
import org.springside.modules.test.benchmark.ConcurrentBenchmark;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * 测试Redis用于Session管理的setEx()与get()方法性能, 使用JSON格式存储数据.
 * 
 * @author calvin
 */
public class RedisSessionBenchmark extends ConcurrentBenchmark {
	private static final int THREAD_COUNT = 20;
	private static final long LOOP_COUNT = 50000;
	private static final int PRINT_BETWEEN_SECONDS = 10;

	private static final String HOST = "localhost";
	private static final int PORT = Protocol.DEFAULT_PORT;
	private static final int TIMEOUT = Protocol.DEFAULT_TIMEOUT;

	private String keyPrefix = "springside.key_";
	private JsonMapper jsonMapper = new JsonMapper();
	private JedisPool pool;

	public static void main(String[] args) throws Exception {
		RedisSessionBenchmark benchmark = new RedisSessionBenchmark(THREAD_COUNT, LOOP_COUNT);
		benchmark.run();
	}

	public RedisSessionBenchmark(int threadCount, long loopCount) {
		super(threadCount, loopCount);
	}

	@Override
	protected void onStart() {
		//create jedis pool
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxActive(THREAD_COUNT);
		pool = new JedisPool(poolConfig, HOST, PORT, TIMEOUT);

		//remove all keys
		Jedis jedis = pool.getResource();
		try {
			jedis.flushDB();
		} finally {
			pool.returnResource(jedis);
		}
	}

	@Override
	protected void onFinish() {
		pool.destroy();
	}

	@Override
	protected BenchmarkTask createTask(int index) {
		return new SessionTask(index, this, PRINT_BETWEEN_SECONDS);
	}

	public class SessionTask extends BenchmarkTask {
		public SessionTask(int index, ConcurrentBenchmark parent, int printBetweenSeconds) {
			super(index, parent, printBetweenSeconds);
		}

		@Override
		public void run() {
			Jedis jedis = pool.getResource();
			onThreadStart();

			try {
				for (int i = 0; i < loopCount; i++) {
					//set session expired after 300 seconds
					String key = new StringBuilder().append(keyPrefix).append(threadIndex).append("_").append(i)
							.toString();
					Session session = new Session(key);
					session.setAttrbute("name", key);
					session.setAttrbute("seq", i);
					jedis.setex(session.getId(), 300, jsonMapper.toJson(session));

					//get it back
					String sessionBackString = jedis.get(key);
					Session sessionBack = jsonMapper.fromJson(sessionBackString, Session.class);

					//print progress message between seconds.
					printProgressMessage(i);
				}
			} finally {
				onThreadFinish();
				pool.returnResource(jedis);
			}
		}
	}
}
