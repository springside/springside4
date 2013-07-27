package org.springside.examples.showcase.demos.redis.job;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.springside.examples.showcase.demos.redis.JedisPoolFactory;
import org.springside.modules.nosql.redis.JedisUtils;
import org.springside.modules.nosql.redis.scheduler.JobManager;
import org.springside.modules.test.benchmark.BenchmarkTask;
import org.springside.modules.test.benchmark.ConcurrentBenchmark;

import redis.clients.jedis.JedisPool;

/**
 * 运行JobManager产生新的Job。
 * 
 * 可用系统参数重置相关变量，@see RedisCounterBenchmark
 * 
 * @author calvin
 */
public class JobProducerDemo extends ConcurrentBenchmark {
	private static final int DEFAULT_THREAD_COUNT = 5;
	private static final long DEFAULT_LOOP_COUNT = 100000;

	private static AtomicLong delaySeconds = new AtomicLong(JobDispatcherDemo.DELAY_SECONDS);
	private static AtomicLong idGenerator = new AtomicLong(0);

	private long expectTps;
	private JedisPool pool;
	private JobManager jobManager;

	public static void main(String[] args) throws Exception {
		JobProducerDemo benchmark = new JobProducerDemo();
		benchmark.execute();
	}

	public JobProducerDemo() {
		super(DEFAULT_THREAD_COUNT, DEFAULT_LOOP_COUNT);
		this.expectTps = Long.parseLong(System.getProperty("benchmark.tps",
				String.valueOf(JobDispatcherDemo.EXPECT_TPS)));
	}

	@Override
	protected void setUp() {
		pool = JedisPoolFactory.createJedisPool(JedisUtils.DEFAULT_HOST, JedisUtils.DEFAULT_PORT,
				JedisUtils.DEFAULT_TIMEOUT, threadCount);
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
			jobManager.scheduleJob("job:" + jobId, delaySeconds.get(), TimeUnit.SECONDS);

			// 达到期望的每秒的TPS后，expireTime往后滚动一秒
			if ((jobId % (expectTps)) == 0) {
				delaySeconds.incrementAndGet();
			}
		}
	}
}
