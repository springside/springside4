package org.springside.examples.showcase.demos.redis;

import org.springside.modules.nosql.redis.JedisTemplate;
import org.springside.modules.test.benchmark.BenchmarkTask;
import org.springside.modules.test.benchmark.ConcurrentBenchmark;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

/**
 * 测试Redis用于计数器时incr()方法的性能.
 * 
 * 可用-Dbenchmark.thread.count, -Dbenchmark.loop.count 重置测试规模
 * 可用-Dbenchmark.host,-Dbenchmark.port,-Dbenchmark.timeout 重置连接参数
 * 
 * @author calvin
 */
public class RedisCounterBenchmark extends ConcurrentBenchmark {
	private static final int DEFAULT_THREAD_COUNT = 50;
	private static final long DEFAULT_LOOP_COUNT = 20000;
	private static final int INTERVAL_IN_SECONDS = 10;

	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = Protocol.DEFAULT_PORT;
	private static final int DEFAULT_TIMEOUT = Protocol.DEFAULT_TIMEOUT;

	private String counterName = "ss.counter";
	private JedisPool pool;
	private JedisTemplate jedisTemplate;

	public static void main(String[] args) throws Exception {
		RedisCounterBenchmark benchmark = new RedisCounterBenchmark();

		benchmark.execute();
	}

	public RedisCounterBenchmark() {
		super(DEFAULT_THREAD_COUNT, DEFAULT_LOOP_COUNT, INTERVAL_IN_SECONDS);
	}

	@Override
	protected void setUp() {
		pool = Utils.createJedisPool(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_TIMEOUT, threadCount);
		jedisTemplate = new JedisTemplate(pool);
		jedisTemplate.set(counterName, "0");
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
			onThreadStart();

			try {
				for (int i = 1; i <= loopCount; i++) {
					jedisTemplate.incr(counterName);
					printProgressMessage(i);
				}
			} finally {
				onThreadFinish();
			}
		}
	}
}
