/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.demos.redis.job.consumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.springside.modules.nosql.redis.pool.JedisPool;
import org.springside.modules.nosql.redis.pool.JedisPoolBuilder;
import org.springside.modules.nosql.redis.service.scheduler.SimpleJobConsumer;
import org.springside.modules.test.benchmark.ConcurrentBenchmark;

import com.google.common.util.concurrent.RateLimiter;

/**
 * 多线程运行JobConsumer，从"ss.job:ready" list中popup job进行处理。
 * 
 * 可用系统参数-Dthread.count 改变线程数
 * 
 * @author calvin
 */
public class SimpleJobConsumerDemo implements Runnable {

	protected static final int THREAD_COUNT = 10;
	protected static final int PRINT_BETWEEN_SECONDS = 10;

	protected static JedisPool pool;
	protected static int threadCount;

	protected static AtomicLong golbalCounter = new AtomicLong(0);
	protected static AtomicLong golbalPreviousCount = new AtomicLong(0);
	protected static RateLimiter golbalPrintRate = RateLimiter.create(1d / PRINT_BETWEEN_SECONDS);

	protected long localCounter = 0L;
	protected long localPreviousCount = 0L;
	protected RateLimiter localPrintRate = RateLimiter.create(1d / PRINT_BETWEEN_SECONDS);

	private SimpleJobConsumer consumer;

	public static void main(String[] args) throws Exception {

		threadCount = Integer.parseInt(System.getProperty(ConcurrentBenchmark.THREAD_COUNT_NAME,
				String.valueOf(THREAD_COUNT)));

		pool = new JedisPoolBuilder().setUrl("direct://localhost:6379?poolSize=" + threadCount).buildPool();

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

	public SimpleJobConsumerDemo() {
		consumer = new SimpleJobConsumer("ss", pool);
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				String job = consumer.popupJob();
				if (job != null) {
					handleJob(job);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 处理Job的回调函数.
	 */
	public void handleJob(String job) {

		long globalCount = golbalCounter.incrementAndGet();
		localCounter++;

		// print global progress, 所有線程裡只有一個会在10秒內打印一次。
		if (golbalPrintRate.tryAcquire()) {
			System.out.printf("Total pop %,d jobs, tps is %,d%n", globalCount,
					(globalCount - golbalPreviousCount.get()) / PRINT_BETWEEN_SECONDS);
			golbalPreviousCount.set(globalCount);
		}

		// print current thread progress，10秒內打印一次。
		if (localPrintRate.tryAcquire()) {
			System.out.printf("Local thread pop %,d jobs, tps is %,d%n", localCounter,
					(localCounter - localPreviousCount) / PRINT_BETWEEN_SECONDS);
			localPreviousCount = localCounter;
		}
	}
}
