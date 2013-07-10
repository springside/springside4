package org.springside.examples.showcase.demos.redis.job;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.springside.examples.showcase.demos.redis.JedisPoolFactory;
import org.springside.modules.nosql.redis.scheduler.SchedulerManager;
import org.springside.modules.test.benchmark.BenchmarkTask;
import org.springside.modules.test.benchmark.ConcurrentBenchmark;

import redis.clients.jedis.JedisPool;

/**
 * 将Job放入ss.job:slepping(sorted set).
 * 
 * 可用-Dbenchmark.thread.count, -Dbenchmark.loop.count 重置测试规模
 * 可用-Dbenchmark.host,-Dbenchmark.port,-Dbenchmark.timeout 重置连接参数
 * 
 * @author calvin
 */
public class JobProducer extends ConcurrentBenchmark {
	private static final int DEFAULT_THREAD_COUNT = 5;
	private static final long DEFAULT_LOOP_COUNT = 5000 * 60;
	private static final int INTERVAL_IN_SECONDS = 10;

	private static AtomicLong idGenerator = new AtomicLong(0);
	private static long delayInSeconds = 0;
	private JedisPool pool;
	private SchedulerManager scheduler;

	public static void main(String[] args) throws Exception {
		JobProducer benchmark = new JobProducer();
		benchmark.execute();
	}

	public JobProducer() {
		super(DEFAULT_THREAD_COUNT, DEFAULT_LOOP_COUNT, INTERVAL_IN_SECONDS);
	}

	@Override
	protected void setUp() {
		// create jedis pool
		pool = JedisPoolFactory.createJedisPool(JobManager.DEFAULT_HOST, JobManager.DEFAULT_PORT, JobManager.DEFAULT_TIMEOUT,
				threadCount);
		scheduler = new SchedulerManager(pool, "ss");
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

			onThreadStart();

			try {
				for (int i = 0; i < loopCount; i++) {
					long jobId = idGenerator.getAndIncrement();
					scheduler.scheduleJob("job:" + jobId, delayInSeconds, TimeUnit.SECONDS);

					// 达到TPS上限后，expireTime往后滚动一秒
					if ((jobId % (JobManager.EXPECT_TPS / threadCount)) == 0) {
						delayInSeconds += 1;
					}
					printProgressMessage(i);
				}
			} finally {
				onThreadFinish();
			}
		}
	}
}
