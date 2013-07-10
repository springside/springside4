package org.springside.examples.showcase.demos.redis.job;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.springside.examples.showcase.demos.redis.JedisPoolFactory;
import org.springside.modules.nosql.redis.scheduler.JobManager;
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
public class JobProducerDemo extends ConcurrentBenchmark {
	private static final int DEFAULT_THREAD_COUNT = 5;
	private static final long DEFAULT_LOOP_COUNT = 5000 * 60;

	private static long delayInSeconds = JobDispatcherDemo.DELAY_SECONDS;
	private static AtomicLong idGenerator = new AtomicLong(0);
	private JedisPool pool;
	private JobManager jobManager;

	public static void main(String[] args) throws Exception {
		JobProducerDemo benchmark = new JobProducerDemo();
		benchmark.execute();
	}

	public JobProducerDemo() {
		super(DEFAULT_THREAD_COUNT, DEFAULT_LOOP_COUNT);
	}

	@Override
	protected void setUp() {
		// create jedis pool
		pool = JedisPoolFactory.createJedisPool(JobDispatcherDemo.DEFAULT_HOST, JobDispatcherDemo.DEFAULT_PORT,
				JobDispatcherDemo.DEFAULT_TIMEOUT, threadCount);
		jobManager = new JobManager("ss", pool);
	}

	@Override
	protected void tearDown() {
		pool.destroy();
	}

	@Override
	protected BenchmarkTask createTask() {
		return new JobProducerTask();
	}

	public class JobProducerTask extends BenchmarkTask {
		@Override
		public void execute(final int requestSequence) {
			long jobId = idGenerator.getAndIncrement();
			jobManager.scheduleJob("job:" + jobId, delayInSeconds, TimeUnit.SECONDS);

			// 达到TPS上限后，expireTime往后滚动一秒
			if ((jobId % (JobDispatcherDemo.EXPECT_TPS)) == 0) {
				delayInSeconds += 1;
			}

		}
	}
}
