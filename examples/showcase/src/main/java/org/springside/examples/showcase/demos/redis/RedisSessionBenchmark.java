package org.springside.examples.showcase.demos.redis;

import java.security.SecureRandom;

import org.springside.modules.mapper.JsonMapper;
import org.springside.modules.nosql.redis.JedisTemplate;
import org.springside.modules.nosql.redis.JedisTemplate.JedisActionNoResult;
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
	private static final int INTERVAL_IN_SECONDS = 10;

	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = Protocol.DEFAULT_PORT;
	private static final int DEFAULT_TIMEOUT = Protocol.DEFAULT_TIMEOUT;

	private String keyPrefix = "ss.session:";
	private JsonMapper jsonMapper = new JsonMapper();
	private JedisPool pool;
	private JedisTemplate jedisTemplate;

	public static void main(String[] args) throws Exception {
		RedisSessionBenchmark benchmark = new RedisSessionBenchmark(DEFAULT_THREAD_COUNT, DEFAULT_LOOP_COUNT,
				INTERVAL_IN_SECONDS);
		benchmark.execute();
	}

	public RedisSessionBenchmark(int defaultThreadCount, long defaultLoopCount, int intervalInSeconds) {
		super(defaultThreadCount, defaultLoopCount, intervalInSeconds);
	}

	@Override
	protected void setUp() {
		// create jedis pool
		pool = Utils.createJedisPool(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_TIMEOUT, threadCount);
		jedisTemplate = new JedisTemplate(pool);

		// remove all keys
		jedisTemplate.execute(new JedisActionNoResult() {
			@Override
			public void action(Jedis jedis) {
				jedis.flushDB();
			}
		});
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
			onThreadStart();

			try {
				for (int i = 1; i <= loopCount; i++) {
					final int seq = i;
					final int randomIndex = random.nextInt((int) loopCount);
					final String key = new StringBuilder().append(keyPrefix).append(taskSequence).append(":")
							.append(randomIndex).toString();

					jedisTemplate.execute(new JedisActionNoResult() {
						@Override
						public void action(Jedis jedis) {
							Session session = new Session(key);
							session.setAttrbute("name", key);
							session.setAttrbute("seq", seq);
							// set session expired after 300 seconds
							jedis.setex(session.getId(), 300, jsonMapper.toJson(session));

							// also get it back
							String sessionBackString = jedis.get(key);
							Session sessionBack = jsonMapper.fromJson(sessionBackString, Session.class);
						}
					});

					// print progress message between seconds.
					printProgressMessage(i);
				}
			} finally {
				onThreadFinish();
			}
		}
	}
}
