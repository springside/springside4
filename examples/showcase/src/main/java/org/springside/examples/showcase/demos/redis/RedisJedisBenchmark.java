package org.springside.examples.showcase.demos.redis;

import java.util.Date;

import org.springside.modules.test.benchmark.BenchmarkBase;

import redis.clients.jedis.Jedis;

public class RedisJedisBenchmark extends BenchmarkBase {
	private static final int THREAD_COUNT = 20;
	private static final int LOOP_COUNT = 10000;
	private static final String HOST = "localhost";

	private final String counterName = "springside.counter";

	public static void main(String[] args) throws Exception {
		RedisJedisBenchmark benchmark = new RedisJedisBenchmark(THREAD_COUNT, LOOP_COUNT);
		benchmark.run();
	}

	public RedisJedisBenchmark(int threadCount, int loopCount) {
		super(threadCount, loopCount);
	}

	@Override
	protected void onStart() {
		//reset counter
		Jedis jedis = new Jedis(HOST);
		jedis.set(counterName, "0");
		jedis.disconnect();
	}

	@Override
	protected Runnable getTask() {
		return new IncTask();
	}

	public class IncTask implements Runnable {

		@Override
		public void run() {
			Jedis jedis = new Jedis(HOST);

			Date startTime = onThreadStart();

			// start test loop
			for (int i = 0; i < loopCount; i++) {
				jedis.incr(counterName);
			}

			onThreadFinish(startTime);
			jedis.disconnect();
		}
	}
}
