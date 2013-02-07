package org.springside.examples.showcase.demos.redis.sessionTimer;

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
public class RedisSessionTimerConsumer implements Runnable {

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
			RedisSessionTimerConsumer consumer = new RedisSessionTimerConsumer();
			threadPool.submit(consumer);
		}

		System.out.println("Hit enter to stop");
		System.in.read();
		System.out.println("Shuting down");
		threadPool.shutdownNow();
		threadPool.awaitTermination(5, TimeUnit.SECONDS);

		tearDown();
	}

	public static void setUp() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxActive(THREAD_COUNT);
		pool = new JedisPool(poolConfig, RedisSessionTimerDistributor.HOST, RedisSessionTimerDistributor.PORT,
				RedisSessionTimerDistributor.TIMEOUT);
	}

	public static void tearDown() {
		//TODO: can't destroy
		System.out.println("Destroy connections");
		pool.destroy();
	}

	@Override
	public void run() {
		Jedis jedis = pool.getResource();
		try {
			while (true) {
				//fetch job
				List<String> result = jedis.brpop(Integer.MAX_VALUE, RedisSessionTimerDistributor.JOB_KEY);
				String id = result.get(0);

				//ack job
				jedis.zrem(RedisSessionTimerDistributor.ACK_KEY, id);
				int count = counter.incrementAndGet();
				int localCount = localCounter.incrementAndGet();

				//print global progress
				if (printRate.tryAcquire()) {
					System.out.printf("Total pop %,d jobs, tps is %,d\n", count, (count - lastPrintCount)
							/ PRINT_BETWEEN_SECONDS);
					lastPrintCount = count;
				}

				//print current thread progress
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
