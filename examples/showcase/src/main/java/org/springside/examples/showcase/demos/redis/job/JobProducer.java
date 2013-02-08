package org.springside.examples.showcase.demos.redis.job;

import java.util.concurrent.atomic.AtomicLong;

import org.springside.modules.test.benchmark.BenchmarkTask;
import org.springside.modules.test.benchmark.ConcurrentBenchmark;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 将Job放入ss.job:schedule(sorted set).
 * 
 * @author calvin
 */
public class JobProducer extends ConcurrentBenchmark {
	private static final int THREAD_COUNT = 5;
	private static final long LOOP_COUNT = 5000 * 60;
	private static final int PRINT_BETWEEN_SECONDS = 10;

	private static AtomicLong idGenerator = new AtomicLong(0);
	private static long expireTime;

	private JedisPool pool;

	public static void main(String[] args) throws Exception {
		JobProducer benchmark = new JobProducer(THREAD_COUNT, LOOP_COUNT);
		benchmark.run();
	}

	public JobProducer(int threadCount, long loopCount) {
		super(threadCount, loopCount);
	}

	@Override
	protected void setUp() {
		//create jedis pool
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxActive(THREAD_COUNT);
		pool = new JedisPool(poolConfig, JobManager.HOST, JobManager.PORT, JobManager.TIMEOUT);
		expireTime = System.currentTimeMillis() + JobManager.DELAY_SECONDS * 1000;
	}

	@Override
	protected void tearDown() {
		pool.destroy();
	}

	@Override
	protected BenchmarkTask createTask(int index) {
		return new JobProducerTask(index, this, PRINT_BETWEEN_SECONDS);
	}

	public class JobProducerTask extends BenchmarkTask {

		public JobProducerTask(int index, ConcurrentBenchmark parent, int printBetweenSeconds) {
			super(index, parent, printBetweenSeconds);
		}

		@Override
		public void run() {
			Jedis jedis = pool.getResource();
			onThreadStart();

			try {
				for (int i = 0; i < loopCount; i++) {
					long jobId = idGenerator.getAndIncrement();
					jedis.zadd(JobManager.TIMER_KEY, expireTime, String.valueOf(jobId));

					//达到TPS上限后，expireTime往后滚动一秒
					if (jobId % JobManager.EXPECT_TPS == 0) {
						expireTime += 1000;
					}
					printProgressMessage(i);
				}
			} finally {
				onThreadFinish();
				pool.returnResource(jedis);
			}
		}
	}
}
