package org.springside.examples.showcase.demos.redis;

import java.util.Date;

import org.springside.modules.test.benchmark.BenchmarkBase;

import redis.clients.jedis.Jedis;

/**
 * 测试Redis用于做Session管理的setEx()与get()方法的性能.
 * 
 * @author calvin
 */
public class RedisSessionBenchmark extends BenchmarkBase {
	private static final int THREAD_COUNT = 20;
	private static final int LOOP_COUNT = 10000;
	private static final String HOST = "localhost";

	private final String keyPrefix = "springside.key";

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
	protected Runnable getTask() {
		return new SessionTask();
	}

	public class SessionTask implements Runnable {

		@Override
		public void run() {
			Jedis jedis = new Jedis(HOST);
			try {
				Date startTime = onThreadStart();

				// start test loop
				for (int i = 0; i < loopCount; i++) {
					//set session expired after 100 seconds
					jedis.setex(keyPrefix + i, 100, String.valueOf(i));
					//get it back
					jedis.get(keyPrefix + i);
				}

				onThreadFinish(startTime);
			} finally {
				jedis.disconnect();
			}
		}
	}
}
