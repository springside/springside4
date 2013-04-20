package org.springside.examples.showcase.demos.redis;

import org.springside.modules.test.benchmark.BenchmarkTask;
import org.springside.modules.test.benchmark.ConcurrentBenchmark;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

/**
 * 测试Redis用于计数器时incr()方法的性能.
 * 
 * 可用-Dbenchmark.thread.count,-Dbenchmark.loop.count 重置测试规模
 * 可用-Dbenchmark.host,-Dbenchmark.port,-Dbenchmark.timeout 重置连接参数
 * 
 * @author calvin
 */
public class RedisCounterBenchmark extends ConcurrentBenchmark {
	private static final int DEFAULT_THREAD_COUNT = 50;
	private static final long DEFAULT_LOOP_COUNT = 20000;
	private static final int PRINT_BETWEEN_SECONDS = 10;

	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = Protocol.DEFAULT_PORT;
	private static final int DEFAULT_TIMEOUT = Protocol.DEFAULT_TIMEOUT;

	private String counterName = "ss.counter";
	private JedisPool pool;

	public static void main(String[] args) throws Exception {
		RedisCounterBenchmark benchmark = new RedisCounterBenchmark(DEFAULT_THREAD_COUNT, DEFAULT_LOOP_COUNT,
				PRINT_BETWEEN_SECONDS);
		benchmark.execute();
	}

	public RedisCounterBenchmark(int defaultThreadCount, long defaultLoopCount, int printBetweenSeconds) {
		super(defaultThreadCount, defaultLoopCount, printBetweenSeconds);
	}

	@Override
	protected void setUp() {

		pool = Utils.createJedisPool(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_TIMEOUT, threadCount);

		//reset counter
		Jedis jedis = pool.getResource();
		try {
			jedis.set(counterName, "0");
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
		return new CounterTask(taskSequence, this);
	}

	public class CounterTask extends BenchmarkTask {

		public CounterTask(int taskSequence, ConcurrentBenchmark parent) {
			super(taskSequence, parent);
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
