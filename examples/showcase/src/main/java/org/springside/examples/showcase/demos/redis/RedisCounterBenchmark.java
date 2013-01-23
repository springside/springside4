package org.springside.examples.showcase.demos.redis;

import org.springside.modules.test.benchmark.BenchmarkTask;
import org.springside.modules.test.benchmark.ConcurrentBenchmark;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * 测试Redis用于计数器时incr()方法的性能.
 * 
 * @author calvin
 */
public class RedisCounterBenchmark extends ConcurrentBenchmark {
	private static final int THREAD_COUNT = 20;
	private static final long LOOP_COUNT = 50000;
	private static final int PRINT_BETWEEN_SECONDS = 10;

	private static final String HOST = "localhost";
	private static final int PORT = Protocol.DEFAULT_PORT;
	private static final int TIMEOUT = Protocol.DEFAULT_TIMEOUT;

	private String counterName = "springside.counter";
	private JedisPool pool;

	public static void main(String[] args) throws Exception {
		RedisCounterBenchmark benchmark = new RedisCounterBenchmark(THREAD_COUNT, LOOP_COUNT);
		benchmark.run();
	}

	public RedisCounterBenchmark(int threadCount, long loopCount) {
		super(threadCount, loopCount);
	}

	@Override
	protected void onStart() {
		//create jedis pool
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxActive(THREAD_COUNT);
		pool = new JedisPool(poolConfig, HOST, PORT, TIMEOUT);

		//reset counter
		Jedis jedis = pool.getResource();
		try {
			jedis.set(counterName, "0");
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
		return new CounterTask(index, this, PRINT_BETWEEN_SECONDS);
	}

	public class CounterTask extends BenchmarkTask {

		public CounterTask(int index, ConcurrentBenchmark parent, int printBetweenSeconds) {
			super(index, parent, printBetweenSeconds);
		}

		@Override
		public void run() {
			Jedis jedis = pool.getResource();
			onThreadStart();

			try {
				for (int i = 0; i < loopCount; i++) {
					jedis.incr(counterName);
					printProgressMessage(i);
				}
			} finally {
				onThreadFinish();
				pool.returnResource(jedis);
			}
		}
	}
}
