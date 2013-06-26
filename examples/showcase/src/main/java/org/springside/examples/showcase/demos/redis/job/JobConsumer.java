package org.springside.examples.showcase.demos.redis.job;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.google.common.util.concurrent.RateLimiter;

/**
 * 消费者多线程执行BRPOP从ss.job(List)中阻塞取出Job，完成后执行ZREM从ss.ack(Sorted Set)中删除任务实现Ack.
 * 
 * @author calvin
 */
public class JobConsumer implements Runnable {

	private static final int THREAD_COUNT = 10;
	private static final int PRINT_BETWEEN_SECONDS = 10;

	private static JedisPool pool;

	private static AtomicInteger counter = new AtomicInteger(0);
	private static RateLimiter printRate = RateLimiter.create(1d / PRINT_BETWEEN_SECONDS);
	private static int lastPrintCount = 0;

	private AtomicInteger localCounter = new AtomicInteger(0);
	private RateLimiter localPrintRate = RateLimiter.create(1d / PRINT_BETWEEN_SECONDS);
	private int lastLocalPrintCount = 0;

	public static void main(String[] args) throws Exception {

		setUp();

		ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
		for (int i = 0; i < THREAD_COUNT; i++) {
			JobConsumer consumer = new JobConsumer();
			threadPool.submit(consumer);
		}

		System.out.println("Hit enter to stop");
		while (true) {
			char c = (char) System.in.read();
			if (c == '\n') {
				System.out.println("Shuting down");
				threadPool.shutdownNow();
				boolean shutdownSucess = threadPool.awaitTermination(5, TimeUnit.SECONDS);
				tearDown();
				if (!shutdownSucess) {
					System.out.println("Forcing exiting.");
					System.exit(-1);
				}
			}
		}

	}

	public static void setUp() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxActive(THREAD_COUNT);
		pool = new JedisPool(poolConfig, JobManager.DEFAULT_HOST, JobManager.DEFAULT_PORT, JobManager.DEFAULT_TIMEOUT);
	}

	public static void tearDown() {
		pool.destroy();
	}

	@Override
	public void run() {
		Jedis jedis = pool.getResource();
		try {
			// Jedis的brpop 不会被中断, 所以下面的判断基本没用, 全靠外围的强行退出.
			while (!Thread.currentThread().isInterrupted()) {
				// fetch job
				List<String> result = jedis.brpop(0, JobManager.JOB_KEY);
				String id = result.get(1);

				// ack job
				jedis.zrem(JobManager.ACK_KEY, id);
				int count = counter.incrementAndGet();
				int localCount = localCounter.incrementAndGet();

				// print global progress
				if (printRate.tryAcquire()) {
					System.out.printf("Total pop %,d jobs, tps is %,d\n", count, (count - lastPrintCount)
							/ PRINT_BETWEEN_SECONDS);
					lastPrintCount = count;
				}

				// print current thread progress
				if (localPrintRate.tryAcquire()) {
					System.out.printf("Local thread pop %,d jobs, tps is %,d\n", localCount,
							(localCount - lastLocalPrintCount) / PRINT_BETWEEN_SECONDS);
					lastLocalPrintCount = localCount;
				}

			}
		} finally {
			pool.returnResource(jedis);
		}
	}
}
