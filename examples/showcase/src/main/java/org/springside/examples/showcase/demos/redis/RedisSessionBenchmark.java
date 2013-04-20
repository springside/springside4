package org.springside.examples.showcase.demos.redis;

import java.security.SecureRandom;

import org.springside.modules.mapper.JsonMapper;
import org.springside.modules.test.benchmark.BenchmarkTask;
import org.springside.modules.test.benchmark.ConcurrentBenchmark;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

/**
 * 测试Redis用于Session管理的setEx()与get()方法性能, 使用JSON格式存储数据.
 * 
 * 可用系统参数重置相关变量，@see RedisCounterBenchmark
 * 
 * @author calvin
 */
public class RedisSessionBenchmark extends ConcurrentBenchmark {
	private static final int DEFAULT_THREAD_COUNT = 50;
	private static final long DEFAULT_LOOP_COUNT = 20000;
	private static final int PRINT_BETWEEN_SECONDS = 10;

	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = Protocol.DEFAULT_PORT;
	private static final int DEFAULT_TIMEOUT = Protocol.DEFAULT_TIMEOUT;

	private String keyPrefix = "ss.session:";
	private JsonMapper jsonMapper = new JsonMapper();
	private JedisPool pool;

	public static void main(String[] args) throws Exception {
		RedisSessionBenchmark benchmark = new RedisSessionBenchmark(DEFAULT_THREAD_COUNT, DEFAULT_LOOP_COUNT,
				PRINT_BETWEEN_SECONDS);
		benchmark.execute();
	}

	public RedisSessionBenchmark(int defaultThreadCount, long defaultLoopCount, int printBetweenSeconds) {
		super(defaultThreadCount, defaultLoopCount, printBetweenSeconds);
	}

	@Override
	protected void setUp() {
		//create jedis pool
		pool = Utils.createJedisPool(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_TIMEOUT, threadCount);

		//remove all keys
		Jedis jedis = pool.getResource();
		try {
			jedis.flushDB();
		} finally {
			pool.returnResource(jedis);
		}
	}

	@Override
	protected void tearDown() {
		pool.destroy();
	}

	@Override
	protected BenchmarkTask createTask(int taskSequence) {
		return new SessionTask(taskSequence, this);
	}

	public class SessionTask extends BenchmarkTask {
		private SecureRandom random = new SecureRandom();

		public SessionTask(int taskSequence, ConcurrentBenchmark parent) {
			super(taskSequence, parent);
		}

		@Override
		public void run() {
			Jedis jedis = pool.getResource();
			onThreadStart();

			try {
				for (int i = 0; i < loopCount; i++) {
					int randomIndex = random.nextInt((int) loopCount);
					String key = new StringBuilder().append(keyPrefix).append(taskSequence).append(":")
							.append(randomIndex).toString();
					Session session = new Session(key);
					session.setAttrbute("name", key);
					session.setAttrbute("seq", i);
					//set session expired after 300 seconds
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
