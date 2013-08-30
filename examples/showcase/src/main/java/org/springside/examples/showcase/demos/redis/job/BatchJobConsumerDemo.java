package org.springside.examples.showcase.demos.redis.job;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springside.examples.showcase.demos.redis.JedisPoolFactory;
import org.springside.modules.nosql.redis.JedisUtils;
import org.springside.modules.nosql.redis.scheduler.JobConsumer;

/**
 * 多线程运行JobConsumer，从"ss.job:ready" list中popup job进行处理。
 * 
 * 可用系统参数重置相关变量，@see RedisCounterBenchmark
 * 
 * @author calvin
 */
public class BatchJobConsumerDemo extends SimpleJobConsumerDemo {

	public static void main(String[] args) throws Exception {

		pool = JedisPoolFactory.createJedisPool(JedisUtils.DEFAULT_HOST, JedisUtils.DEFAULT_PORT,
				JedisUtils.DEFAULT_TIMEOUT, THREAD_COUNT);

		ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
		for (int i = 0; i < THREAD_COUNT; i++) {
			JobConsumer consumer = new JobConsumer("ss", pool, new BatchJobConsumerDemo());
			// set it as true
			consumer.setReliable(true);
			// set it to 10
			consumer.setBatchSize(10);
			threadPool.execute(consumer);
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

}
