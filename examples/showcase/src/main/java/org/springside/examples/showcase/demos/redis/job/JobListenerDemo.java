package org.springside.examples.showcase.demos.redis.job;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.springside.examples.showcase.demos.redis.JedisPoolFactory;
import org.springside.modules.nosql.redis.scheduler.JobListener;
import org.springside.modules.nosql.redis.scheduler.JobListener.JobHandler;

import redis.clients.jedis.JedisPool;

import com.google.common.util.concurrent.RateLimiter;

/**
 * 消费者多线程执行BRPOP从ss.job(List)中阻塞取出Job，完成后执行ZREM从ss.ack(Sorted Set)中删除任务实现Ack.
 * 
 * @author calvin
 */
public class JobListenerDemo implements JobHandler {

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

		pool = JedisPoolFactory.createJedisPool(JobDispatcherDemo.DEFAULT_HOST, JobDispatcherDemo.DEFAULT_PORT,
				JobDispatcherDemo.DEFAULT_TIMEOUT, THREAD_COUNT);

		ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
		for (int i = 0; i < THREAD_COUNT; i++) {
			JobListener listener = new JobListener("ss", pool, new JobListenerDemo());
			threadPool.submit(listener);
		}

		System.out.println("Hit enter to stop");
		try {
			while (true) {
				char c = (char) System.in.read();
				if (c == '\n') {
					System.out.println("Shutting down");
					threadPool.shutdownNow();
					boolean shutdownSucess = threadPool.awaitTermination(JobListener.POPUP_TIMEOUT + 1,
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

	@Override
	public void receiveJob(String job) {
		long globalCount = golbalCounter.incrementAndGet();
		localCounter++;

		// print global progress, 所有線程裡只有一個需要在10秒內打印
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
