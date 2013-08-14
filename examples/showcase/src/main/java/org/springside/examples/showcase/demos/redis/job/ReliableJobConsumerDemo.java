package org.springside.examples.showcase.demos.redis.job;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.springside.examples.showcase.demos.redis.JedisPoolFactory;
import org.springside.modules.nosql.redis.JedisUtils;
import org.springside.modules.nosql.redis.scheduler.JobConsumer;
import org.springside.modules.nosql.redis.scheduler.JobConsumer.JobHandler;

import redis.clients.jedis.JedisPool;

import com.google.common.util.concurrent.RateLimiter;

/**
 * 多线程运行JobConsumer，从"ss.job:ready" list中popup job进行处理。
 * 
 * 可用系统参数重置相关变量，@see RedisCounterBenchmark
 * 
 * @author calvin
 */
public class ReliableJobConsumerDemo implements JobHandler {

	private static final int THREAD_COUNT = 10;
	private static final int PRINT_BETWEEN_SECONDS = 10;

	private static JedisPool pool;

	private static AtomicLong golbalCounter = new AtomicLong(0);
	private static AtomicLong golbalPreviousCount = new AtomicLong(0);
	private static RateLimiter golbalPrintRate = RateLimiter.create(1d / PRINT_BETWEEN_SECONDS);

	private long localCounter = 0L;
	private long localPreviousCount = 0L;
	private RateLimiter localPrintRate = RateLimiter.create(1d / PRINT_BETWEEN_SECONDS);

	public static void main(String[] args) throws Exception {

		pool = JedisPoolFactory.createJedisPool(JedisUtils.DEFAULT_HOST, JedisUtils.DEFAULT_PORT,
				JedisUtils.DEFAULT_TIMEOUT, THREAD_COUNT);

		ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
		for (int i = 0; i < THREAD_COUNT; i++) {
			JobConsumer consumer = new JobConsumer("ss", pool, new ReliableJobConsumerDemo());
			consumer.setReliable(true);
			threadPool.submit(consumer);
		}

		System.out.println("Hit enter to stop");
		try {
			while (true) {
				char c = (char) System.in.read();
				if (c == '\n') {
					System.out.println("Shutting down");
					threadPool.shutdownNow();
					boolean shutdownSucess = threadPool.awaitTermination(JobConsumer.DEFAULT_POPUP_TIMEOUT_SECONDS + 1,
							TimeUnit.SECONDS);

					if (!shutdownSucess) {
						System.out.println("Forcing exiting.");
						System.exit(-1);
					}

					return;
				}
			}
		} finally {
			pool.destroy();
		}
	}

	/**
	 * 处理Job的回调函数.
	 */
	@Override
	public void handleJob(String job) {
		long globalCount = golbalCounter.incrementAndGet();
		localCounter++;

		// print global progress, 所有線程裡只有一個线程会在10秒內打印一次。
		if (golbalPrintRate.tryAcquire()) {
			System.out.printf("Total pop %,d jobs, tps is %,d\n", globalCount,
					(globalCount - golbalPreviousCount.get()) / PRINT_BETWEEN_SECONDS);
			golbalPreviousCount.set(globalCount);
		}

		// print current thread progress，10秒內打印一次。
		if (localPrintRate.tryAcquire()) {
			System.out.printf("Local thread pop %,d jobs, tps is %,d\n", localCounter,
					(localCounter - localPreviousCount) / PRINT_BETWEEN_SECONDS);
			localPreviousCount = localCounter;
		}
	}
}
