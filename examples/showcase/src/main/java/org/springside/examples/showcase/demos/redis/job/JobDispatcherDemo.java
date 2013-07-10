package org.springside.examples.showcase.demos.redis.job;

import org.springside.examples.showcase.demos.redis.JedisPoolFactory;
import org.springside.modules.nosql.redis.scheduler.JobDispatcher;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

/**
 * 每秒执行一次Lua Script，从ss.timer(Sorted Set)中取出到时的任务，放入ss.job(List)和ss.ack(Sorted Set)中等待领取和完成呢个确认.
 * 
 * @author calvin
 */
public class JobDispatcherDemo {

	public static final int EXPECT_TPS = 5000;
	public static final int DELAY_SECONDS = 10;

	public static final String DEFAULT_HOST = "localhost";
	public static final int DEFAULT_PORT = Protocol.DEFAULT_PORT;
	public static final int DEFAULT_TIMEOUT = 5000;

	public static void main(String[] args) throws Exception {

		JedisPool pool = JedisPoolFactory.createJedisPool(JobDispatcherDemo.DEFAULT_HOST,
				JobDispatcherDemo.DEFAULT_PORT, JobDispatcherDemo.DEFAULT_TIMEOUT, 1);
		try {
			JobDispatcher jobDispatcher = new JobDispatcher("ss", pool);
			jobDispatcher.start(1000);
			System.out.println("Hit enter to stop.");
			while (true) {
				char c = (char) System.in.read();
				if (c == '\n') {
					System.out.println("Shuting down");
					jobDispatcher.stop();
					return;
				}
			}

		} finally {
			pool.destroy();
		}
	}
}
