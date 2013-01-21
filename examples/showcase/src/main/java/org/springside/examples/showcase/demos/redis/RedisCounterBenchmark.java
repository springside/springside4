package org.springside.examples.showcase.demos.redis;

import java.util.Date;

import org.springside.modules.test.benchmark.BenchmarkBase;

import redis.clients.jedis.Jedis;

/**
 * 测试Redis用于做计数器的incr()方法性能.
 * 
 * @author calvin
 */
public class RedisCounterBenchmark extends BenchmarkBase {
	private static final int THREAD_COUNT = 20;
	private static final int LOOP_COUNT = 10000;
	private static final int COUNTERS = 1;
	private static final String HOST = "localhost";

	private final String counterName = "springside.counter";

	public static void main(String[] args) throws Exception {
		RedisCounterBenchmark benchmark = new RedisCounterBenchmark(THREAD_COUNT, LOOP_COUNT);
		benchmark.run();
	}

	public RedisCounterBenchmark(int threadCount, int loopCount) {
		super(threadCount, loopCount);
	}

	@Override
	protected void onStart() {
		//remove all keys
		Jedis jedis = new Jedis(HOST);
		jedis.set(counterName, "0");
		jedis.disconnect();
	}

	@Override
	protected Runnable getTask(int index) {
		return new CounterTask();
	}

	public class CounterTask implements Runnable {

		@Override
		public void run() {
			Jedis jedis = new Jedis(HOST);
			Date startTime = onThreadStart();
			try {
				// start test loop
				for (int i = 0; i < loopCount; i++) {
					jedis.incr(counterName + (i % COUNTERS));
				}
			} finally {
				onThreadFinish(startTime);
				jedis.disconnect();
			}
		}
	}
}
