package org.springside.examples.showcase.demos.redis;

import java.util.Date;

import org.springside.modules.mapper.JsonMapper;
import org.springside.modules.test.benchmark.BenchmarkBase;

import redis.clients.jedis.Jedis;

/**
 * 测试Redis用于做Session管理的setEx()与get()方法的性能, 并使用JSON格式存储数据.
 * 
 * @author calvin
 */
public class RedisSessionBenchmark extends BenchmarkBase {
	private static final int THREAD_COUNT = 20;
	private static final int LOOP_COUNT = 10000;
	private static final String HOST = "localhost";

	private final String keyPrefix = "springside.key";
	private static JsonMapper jsonMapper = new JsonMapper();

	public static void main(String[] args) throws Exception {
		RedisSessionBenchmark benchmark = new RedisSessionBenchmark(THREAD_COUNT, LOOP_COUNT);
		benchmark.run();
	}

	public RedisSessionBenchmark(int threadCount, int loopCount) {
		super(threadCount, loopCount);
	}

	@Override
	protected void onStart() {
		//reset counter
		Jedis jedis = new Jedis(HOST);
		jedis.disconnect();
	}

	@Override
	protected Runnable getTask(int index) {
		return new SessionTask(index);
	}

	public class SessionTask implements Runnable {
		private final int index;

		public SessionTask(int index) {
			this.index = index;
		}

		@Override
		public void run() {
			Jedis jedis = new Jedis(HOST);
			try {
				Date startTime = onThreadStart();

				// start test loop
				for (int i = 0; i < loopCount; i++) {
					//set session expired after 100 seconds
					Session session = new Session(keyPrefix + i);
					session.addAttrbute("name", "user_" + index + "_" + i);
					session.addAttrbute("age", i);
					jedis.setex(session.getId(), 100, jsonMapper.toJson(session));

					//get it back
					String sessionBackString = jedis.get(keyPrefix + i);
					Session sessionBack = jsonMapper.fromJson(sessionBackString, Session.class);
				}

				onThreadFinish(startTime);
			} finally {
				jedis.disconnect();
			}
		}
	}
}
