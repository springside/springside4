package org.springside.examples.showcase.demos.redis.job;

import org.springside.examples.showcase.demos.redis.JedisPoolFactory;
import org.springside.modules.nosql.redis.JedisUtils;
import org.springside.modules.nosql.redis.scheduler.JobDispatcher;

import redis.clients.jedis.JedisPool;

/**
 * 运行JobDispatcher，每秒将Job从"ss.sleeping" sorted set 发布到"ss.ready" list.
 * 
 * 可用系统参数重置相关变量，@see RedisCounterBenchmark
 * 
 * @author calvin
 */
public class JobDispatcherDemo {

	public static final int EXPECT_TPS = 5000;
	public static final int DELAY_SECONDS = 10;

	public static void main(String[] args) throws Exception {

		JedisPool pool = JedisPoolFactory.createJedisPool(JedisUtils.DEFAULT_HOST, JedisUtils.DEFAULT_PORT,
				JedisUtils.DEFAULT_TIMEOUT, 1);
		try {
			JobDispatcher jobDispatcher = new JobDispatcher("ss", pool);

			printJobNumbers(jobDispatcher);

			jobDispatcher.start();

			System.out.println("Hit enter to stop.");
			while (true) {
				char c = (char) System.in.read();
				if (c == '\n') {
					System.out.println("Shuting down");
					jobDispatcher.stop();
					printJobNumbers(jobDispatcher);
					return;
				}
			}
		} finally {
			pool.destroy();
		}
	}

	private static void printJobNumbers(JobDispatcher jobDispatcher) {
		System.out.printf("Sleeping job %d, Ready Job %d, Dispatch Counter %d \n",
				jobDispatcher.getSleepingJobNumber(), jobDispatcher.getReadyJobNumber(),
				jobDispatcher.getDispatchNumber());
	}
}
