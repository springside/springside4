package org.springside.examples.showcase.demos.redis.sessionTimer;

import org.springside.modules.test.benchmark.BenchmarkTask;
import org.springside.modules.test.benchmark.ConcurrentBenchmark;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * 将Timer插入sorted set.
 * 
 * @author calvin
 */
public class RedisSessionTimerProducer extends ConcurrentBenchmark {
	private static final int THREAD_COUNT = 50;
	private static final long LOOP_COUNT = 500 * 60;
	private static final int PRINT_BETWEEN_SECONDS = 10;

	private static final String HOST = "localhost";
	private static final int PORT = Protocol.DEFAULT_PORT;
	private static final int TIMEOUT = Protocol.DEFAULT_TIMEOUT;

	private String keyPrefix = "ss.timer";
	private JedisPool pool;

	public static void main(String[] args) throws Exception {
		RedisSessionTimerProducer benchmark = new RedisSessionTimerProducer(THREAD_COUNT, LOOP_COUNT);
		benchmark.run();
	}

	public RedisSessionTimerProducer(int threadCount, long loopCount) {
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
	protected BenchmarkTask createTask(int index) {
		return new SessionTimerProducerTask(index, this, PRINT_BETWEEN_SECONDS);
	}

	public class SessionTimerProducerTask extends BenchmarkTask {

		public SessionTimerProducerTask(int index, ConcurrentBenchmark parent, int printBetweenSeconds) {
			super(index, parent, printBetweenSeconds);
		}

		@Override
		public void run() {
			Jedis jedis = pool.getResource();
			onThreadStart();

			try {

				for (int i = 0; i < loopCount; i++) {
					long scheduleTime = threadIndex * loopCount + i;
					jedis.zadd(keyPrefix, scheduleTime, String.valueOf(scheduleTime));
					printProgressMessage(i);
				}
			} finally {
				onThreadFinish();
				pool.returnResource(jedis);
			}
		}
	}
}
