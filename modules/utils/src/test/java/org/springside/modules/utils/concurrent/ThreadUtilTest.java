/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.utils.concurrent;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.test.log.LogbackListAppender;

public class ThreadUtilTest {
	@Test
	public void buildThreadFactory() {

		Runnable testRunnable = new Runnable() {
			@Override
			public void run() {
			}
		};
		// 测试name格式
		ThreadFactory threadFactory = ThreadUtil.buildThreadFactory("example");
		Thread thread = threadFactory.newThread(testRunnable);

		assertThat(thread.getName()).isEqualTo("example-0");
		assertThat(thread.isDaemon()).isFalse();

		// 测试daemon属性设置
		threadFactory = ThreadUtil.buildThreadFactory("example", true);
		Thread thread2 = threadFactory.newThread(testRunnable);

		assertThat(thread.getName()).isEqualTo("example-0");
		assertThat(thread2.isDaemon()).isTrue();
	}

	@Test
	public void gracefulShutdown() throws InterruptedException {

		Logger logger = LoggerFactory.getLogger("test");
		LogbackListAppender appender = new LogbackListAppender();
		appender.addToLogger("test");

		// time enough to shutdown
		ExecutorService pool = Executors.newSingleThreadExecutor();
		Runnable task = new Task(logger, 500, 0);
		pool.execute(task);
		ThreadUtil.gracefulShutdown(pool, 1000, TimeUnit.MILLISECONDS);
		assertThat(pool.isTerminated()).isTrue();
		assertThat(appender.getFirstLog()).isNull();

		// time not enough to shutdown,call shutdownNow
		appender.clearLogs();
		pool = Executors.newSingleThreadExecutor();
		task = new Task(logger, 1000, 0);
		pool.execute(task);
		ThreadUtil.gracefulShutdown(pool, 500, TimeUnit.MILLISECONDS);
		assertThat(pool.isTerminated()).isTrue();
		assertThat(appender.getFirstLog().getMessage()).isEqualTo("InterruptedException");

		// self thread interrupt while calling gracefulShutdown
		appender.clearLogs();

		final ExecutorService self = Executors.newSingleThreadExecutor();
		task = new Task(logger, 100000, 0);
		self.execute(task);

		final CountDownLatch lock = new CountDownLatch(1);
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				lock.countDown();
				ThreadUtil.gracefulShutdown(self, 200000, TimeUnit.MILLISECONDS);
			}
		});
		thread.start();
		lock.await();
		thread.interrupt();
		ThreadUtil.sleep(500);
		assertThat(appender.getFirstLog().getMessage()).isEqualTo("InterruptedException");
	}

	static class Task implements Runnable {
		private final Logger logger;

		private int runTime = 0;

		private final int sleepTime;

		Task(Logger logger, int sleepTime, int runTime) {
			this.logger = logger;
			this.sleepTime = sleepTime;
			this.runTime = runTime;
		}

		@Override
		public void run() {
			System.out.println("start task");
			if (runTime > 0) {
				long start = System.currentTimeMillis();
				while ((System.currentTimeMillis() - start) < runTime) {
				}
			}

			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				logger.warn("InterruptedException");
			}
		}
	}
}
