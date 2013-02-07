package org.springside.examples.showcase.demos.redis.sessionTimer;

import org.springside.modules.test.benchmark.BenchmarkTask;
import org.springside.modules.test.benchmark.ConcurrentBenchmark;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 将Timer放入ss.timer(Sorted set).
 * 
 * @author calvin
 */
public class RedisSessionTimerProducer extends ConcurrentBenchmark {
	private static final int THREAD_COUNT = 10;
	private static final long LOOP_COUNT = 2500 * 60;
	private static final int PRINT_BETWEEN_SECONDS = 10;

	private JedisPool pool;

	public static void main(String[] args) throws Exception {
		RedisSessionTimerProducer benchmark = new RedisSessionTimerProducer(THREAD_COUNT, LOOP_COUNT);
		benchmark.run();
	}

	public RedisSessionTimerProducer(int threadCount, long loopCount) {
		super(threadCount, loopCount);
	}

	@Override
	protected void setUp() {
		//create jedis pool
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxActive(THREAD_COUNT);
		pool = new JedisPool(poolConfig, RedisSessionTimerDistributor.HOST, RedisSessionTimerDistributor.PORT,
				RedisSessionTimerDistributor.TIMEOUT);
	}

	@Override
	protected void tearDown() {
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
					long scheduleTime = taskSequence * loopCount + i;
					jedis.zadd(RedisSessionTimerDistributor.TIMER_KEY, scheduleTime, String.valueOf(scheduleTime));
					printProgressMessage(i);
				}
			} finally {
				onThreadFinish();
				pool.returnResource(jedis);
			}
		}
	}
}
