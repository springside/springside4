package org.springside.examples.showcase.demos.redis;

import org.springside.modules.mapper.JsonMapper;
import org.springside.modules.test.benchmark.BenchmarkBase;
import org.springside.modules.test.benchmark.BenchmarkTask;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * 测试Redis用于做Session管理的setEx()与get()方法, 并使用JSON格式存储数据.
 * 
 * @author calvin
 */
public class RedisSessionBenchmark extends BenchmarkBase {
	private static final int THREAD_COUNT = 20;
	private static final long LOOP_COUNT = 50000;
	private static final int PRINT_INTERVAL_SECONDS = 10;

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
	protected Runnable getTask(int index) {
		return new SessionTask(index, this, PRINT_INTERVAL_SECONDS);
	}

	public class SessionTask extends BenchmarkTask {
		public SessionTask(int index, BenchmarkBase parent, int printInfoInterval) {
			super(index, parent, printInfoInterval);
		}

		@Override
		public void run() {
			Jedis jedis = pool.getResource();
			onThreadStart();

			try {

				// start test loop
				for (int i = 0; i < loopCount; i++) {
					//set session expired after 100 seconds
					Session session = new Session(keyPrefix + i);
					session.addAttrbute("name", new StringBuilder().append("user_").append(threadIndex).append("_")
							.append(i).toString());
					session.addAttrbute("age", i);
					jedis.setex(session.getId(), 100, jsonMapper.toJson(session));

					//get it back
					String sessionBackString = jedis.get(keyPrefix + i);
					Session sessionBack = jsonMapper.fromJson(sessionBackString, Session.class);

					//print message
					if (i % 10 == 0) {
						printInfo(i);
					}
				}
			} finally {
				onThreadFinish();
				pool.returnResource(jedis);
			}
		}
	}
}
