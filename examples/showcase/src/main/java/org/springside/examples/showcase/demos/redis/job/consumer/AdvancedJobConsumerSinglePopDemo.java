/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.demos.redis.job.consumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springside.examples.showcase.demos.redis.JedisPoolFactory;
import org.springside.modules.nosql.redis.JedisUtils;
import org.springside.modules.nosql.redis.scheduler.AdvancedJobConsumer;
import org.springside.modules.nosql.redis.scheduler.SimpleJobConsumer;
import org.springside.modules.test.benchmark.ConcurrentBenchmark;
import org.springside.modules.utils.Threads;

/**
 * 多线程运行ReliableJobConsumer，从"ss.job:ready" list中popup job进行处理。
 * 
 * 可用系统参数benchmark.thread.count 改变线程数.
 * 
 * @author calvin
 */
public class AdvancedJobConsumerSinglePopDemo extends SimpleJobConsumerDemo {

	private AdvancedJobConsumer consumer;

	public static void main(String[] args) throws Exception {

		threadCount = Integer.parseInt(System.getProperty(ConcurrentBenchmark.THREAD_COUNT_NAME,
				String.valueOf(THREAD_COUNT)));

		pool = JedisPoolFactory.createJedisPool(JedisUtils.DEFAULT_HOST, JedisUtils.DEFAULT_PORT,
				JedisUtils.DEFAULT_TIMEOUT, threadCount);

		ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);
		for (int i = 0; i < threadCount; i++) {
			SimpleJobConsumerDemo demo = new SimpleJobConsumerDemo();
			threadPool.execute(demo);
		}

		System.out.println("Hit enter to stop");
		try {
			while (true) {
				char c = (char) System.in.read();
				if (c == '\n') {
					System.out.println("Shutting down");
					threadPool.shutdownNow();
					boolean shutdownSucess = threadPool.awaitTermination(
							SimpleJobConsumer.DEFAULT_POPUP_TIMEOUT_SECONDS + 1, TimeUnit.SECONDS);

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

	public AdvancedJobConsumerSinglePopDemo() {
		consumer = new AdvancedJobConsumer("ss", pool);
		consumer.setReliable(true);
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				String job = consumer.popupJob();
				if (job != null) {
					handleJob(job);
				} else {
					Threads.sleep(100);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void handleJob(String job) {
		super.handleJob(job);
		consumer.ackJob(job);
	}
}
