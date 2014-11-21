/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.demos.redis.job.consumer;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springside.modules.nosql.redis.pool.JedisPoolBuilder;
import org.springside.modules.nosql.redis.service.scheduler.AdvancedJobConsumer;
import org.springside.modules.nosql.redis.service.scheduler.SimpleJobConsumer;
import org.springside.modules.test.benchmark.ConcurrentBenchmark;
import org.springside.modules.utils.Threads;

/**
 * 多线程运行BatchJobConsumer，从"ss.job:ready" list中popup job进行处理。
 * 
 * 可用系统参数-Dthread.count 改变线程数，用-Dreliable改变是否高可靠，用-Dbatchsize 改变批处理数量.
 * 
 * @author calvin
 */
public class AdvancedJobConsumerBatchPopDemo extends SimpleJobConsumerDemo {

	private static boolean reliable;
	private static int batchSize;

	private AdvancedJobConsumer consumer;

	public static void main(String[] args) throws Exception {

		threadCount = Integer.parseInt(System.getProperty(ConcurrentBenchmark.THREAD_COUNT_NAME,
				String.valueOf(THREAD_COUNT)));

		reliable = Boolean.parseBoolean(System.getProperty("reliable",
				String.valueOf(AdvancedJobConsumer.DEFAULT_RELIABLE)));
		batchSize = Integer.parseInt(System.getProperty("batchsize",
				String.valueOf(AdvancedJobConsumer.DEFAULT_BATCH_SIZE)));

		pool = new JedisPoolBuilder().setUrl("direct://localhost:6379?poolSize=" + threadCount).buildPool();

		ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);
		for (int i = 0; i < threadCount; i++) {
			AdvancedJobConsumerBatchPopDemo demo = new AdvancedJobConsumerBatchPopDemo();
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

	public AdvancedJobConsumerBatchPopDemo() {
		consumer = new AdvancedJobConsumer("ss", pool);
		consumer.setReliable(reliable);
		consumer.setBatchSize(batchSize);
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				List<String> jobs = consumer.popupJobs();
				if ((jobs != null) && !jobs.isEmpty()) {
					for (String job : jobs) {
						handleJob(job);
					}
				} else {
					Threads.sleep(100);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void handleJob(String job) {
		super.handleJob(job);
		if (reliable) {
			consumer.ackJob(job);
		}
	}
}
