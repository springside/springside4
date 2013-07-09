package org.springside.examples.showcase.demos.redis.job;

import java.util.concurrent.atomic.AtomicLong;

import org.springside.examples.showcase.demos.redis.Utils;
import org.springside.modules.test.benchmark.BenchmarkTask;
import org.springside.modules.test.benchmark.ConcurrentBenchmark;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 将Job放入ss.job:schedule(sorted set).
 * 
 * @author calvin
 */
public class JobProducer extends ConcurrentBenchmark {
	private static final int DEFAULT_THREAD_COUNT = 5;
	private static final long DEFAULT_LOOP_COUNT = 5000 * 60;
	private static final int PRINT_BETWEEN_SECONDS = 10;

	private static AtomicLong idGenerator = new AtomicLong(0);
	private static long expireTime;
	private JedisPool pool;

	public static void main(String[] args) throws Exception {
		JobProducer benchmark = new JobProducer(DEFAULT_THREAD_COUNT, DEFAULT_LOOP_COUNT, PRINT_BETWEEN_SECONDS);
		benchmark.execute();
	}

	public JobProducer(int defaultThreadCount, long defaultLoopCount, int printBetweenSeconds) {
		super(defaultThreadCount, defaultLoopCount, printBetweenSeconds);
	}

	@Override
	protected void setUp() {
		// create jedis pool
		pool = Utils.createJedisPool(JobManager.DEFAULT_HOST, JobManager.DEFAULT_PORT, JobManager.DEFAULT_TIMEOUT,
				threadCount);

		expireTime = System.currentTimeMillis() + (JobManager.DELAY_SECONDS * 1000);
	}

	@Override
	protected void tearDown() {
		pool.destroy();
	}

	@Override
	protected BenchmarkTask createTask(int taskSequence) {
		return new JobProducerTask(taskSequence, this);
	}

	public class JobProducerTask extends BenchmarkTask {

		public JobProducerTask(int taskSequence, ConcurrentBenchmark parent) {
			super(taskSequence, parent);
		}

		@Override
		public void run() {
			Jedis jedis = pool.getResource();
			onThreadStart();

			try {
				for (int i = 0; i < loopCount; i++) {
					long jobId = idGenerator.getAndIncrement();
					jedis.zadd(JobManager.TIMER_KEY, expireTime, String.valueOf(jobId));

					// 达到TPS上限后，expireTime往后滚动一秒
					if ((jobId % JobManager.EXPECT_TPS) == 0) {
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
