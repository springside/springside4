/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.demos.redis.job.dispatcher;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springside.examples.showcase.demos.redis.JedisPoolFactory;
import org.springside.modules.nosql.redis.JedisUtils;
import org.springside.modules.nosql.redis.scheduler.JobDispatcher;
import org.springside.modules.nosql.redis.scheduler.JobStatistics;

import redis.clients.jedis.JedisPool;

/**
 * 运行JobDispatcher，每秒将Job从"job:ss:scheduled" sorted set 发布到"job:ss:ready" list.
 * 
 * 可用系统参数重置相关变量，@see RedisCounterBenchmark
 * 
 * @author calvin
 */
public class SimpleJobDispatcherDemo {

	public static final int EXPECT_TPS = 5000;
	public static final int DELAY_SECONDS = 10;

	private static ScheduledFuture statisticsTask;

	public static void main(String[] args) throws Exception {

		JedisPool pool = JedisPoolFactory.createJedisPool(JedisUtils.DEFAULT_HOST, JedisUtils.DEFAULT_PORT,
				JedisUtils.DEFAULT_TIMEOUT, 1);
		try {
			JobDispatcher dispatcher = new JobDispatcher("ss", pool);
			JobStatistics statistics = new JobStatistics("ss", pool);

			startPrintStatistics(statistics);
			dispatcher.start();

			System.out.println("Hit enter to stop.");
			while (true) {
				char c = (char) System.in.read();
				if (c == '\n') {
					System.out.println("Shuting down");
					dispatcher.stop();
					stopPrintStatistics();
					return;
				}
			}
		} finally {
			pool.destroy();
		}
	}

	private static void startPrintStatistics(final JobStatistics statistics) {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		statisticsTask = scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				System.out.printf("Scheduled job %d, Ready Job %d, Dispatch Counter %d%n",
						statistics.getScheduledJobNumber(), statistics.getReadyJobNumber(),
						statistics.getDispatchCounter());
			}
		}, 0, 10, TimeUnit.SECONDS);
	}

	private static void stopPrintStatistics() {
		statisticsTask.cancel(true);
	}
}
