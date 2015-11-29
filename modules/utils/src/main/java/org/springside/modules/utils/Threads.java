/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * 线程相关工具类.
 * 
 * @author calvin
 */
public class Threads {

	/**
	 * sleep等待, 单位为毫秒, 已捕捉并处理InterruptedException.
	 */
	public static void sleep(long durationMillis) {
		try {
			Thread.sleep(durationMillis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * sleep等待，已捕捉并处理InterruptedException.
	 */
	public static void sleep(long duration, TimeUnit unit) {
		try {
			Thread.sleep(unit.toMillis(duration));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * 按照ExecutorService JavaDoc示例代码编写的Graceful Shutdown方法.
	 * 
	 * 先使用shutdown, 停止接收新任务并尝试完成所有已存在任务.
	 * 
	 * 如果1/2超时时间后, 则调用shutdownNow,取消在workQueue中Pending的任务,并中断所有阻塞函数.
	 * 
	 * 如果1/2超时仍然超時，則強制退出.
	 * 
	 * 另对在shutdown时线程本身被调用中断做了处理.
	 * 
	 * 返回线程最后是否被中断.
	 */
	public static boolean gracefulShutdown(ExecutorService threadPool, int shutdownTimeoutMills) {
		return MoreExecutors.shutdownAndAwaitTermination(threadPool, shutdownTimeoutMills, TimeUnit.MILLISECONDS);
	}

	/**
	 * @see #gracefulShutdown(ExecutorService, int)
	 */
	public static boolean gracefulShutdown(ExecutorService threadPool, int shutdownTimeout, TimeUnit timeUnit) {
		return MoreExecutors.shutdownAndAwaitTermination(threadPool, shutdownTimeout, timeUnit);
	}

	/**
	 * 创建ThreadFactory，使得创建的线程有自己的名字而不是默认的"pool-x-thread-y"
	 * 
	 * 格式如"mythread-%d"，使用了Guava的工具类
	 */
	public static ThreadFactory buildThreadFactory(String nameFormat) {
		return new ThreadFactoryBuilder().setNameFormat(nameFormat).build();
	}

	/**
	 * 可设定是否daemon, daemon线程在主线程已执行完毕时, 不会阻塞应用不退出, 而非daemon线程则会阻塞.
	 * 
	 * @see #buildThreadFactory(String)
	 */
	public static ThreadFactory buildThreadFactory(String nameFormat, boolean daemon) {
		return new ThreadFactoryBuilder().setNameFormat(nameFormat).setDaemon(daemon).build();
	}

	/**
	 * 创建FixedThreadPool, 等价Executors的默认实现.
	 * 
	 * 任务提交时, 如果线程数还没达到nThreads即创建新线程(即n次提交后线程总数必达到n)
	 * 
	 * 线程不会空闲回收.
	 * 
	 * 当所有线程繁忙时, 放入无限长的LinkedBlockingQueue中等待.
	 */
	public static ExecutorService newFixedThreadPool(int nThreads) {
		return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
	}

	/**
	 * @see #newFixedThreadPool(int)
	 */
	public static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
		return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(), threadFactory);
	}

	/**
	 * 创建Queue长度受限的FixedThreadPool.
	 * 
	 * 当到达maxQueue时，调用RejectHandler，默认为AbortPolicy，抛出RejectedExecutionException异常.
	 * 其他可选的Policy包括静默放弃当前任务(Discard)，放弃Queue里最老的任务，或由主线程来直接执行(CallerRuns).
	 * 
	 * @see #newFixedThreadPool(int)
	 */
	public static ExecutorService newFixedThreadPool(int nThreads, int maxQueueSize) {
		return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
				new ArrayBlockingQueue<Runnable>(maxQueueSize));
	}

	/**
	 * @see #newFixedThreadPool(int, int)
	 */
	public static ExecutorService newFixedThreadPool(int nThreads, int maxQueueSize, ThreadFactory threadFactory) {
		return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
				new ArrayBlockingQueue<Runnable>(maxQueueSize), threadFactory);
	}

	/**
	 * @see #newFixedThreadPool(int, int)
	 */
	public static ExecutorService newFixedThreadPool(int nThreads, int maxQueueSize, ThreadFactory threadFactory,
			RejectedExecutionHandler rejectHanlder) {
		return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
				new ArrayBlockingQueue<Runnable>(maxQueueSize), threadFactory, rejectHanlder);
	}

	/**
	 * 创建CachedThreadPool,等价Executors的默认实现.
	 * 
	 * 任务提交时, 如果没有空闲线程, 立刻创建新线程, 总线程数无上限.
	 * 
	 * 如果线程空闲超过一分钟, 进行回收.
	 */
	public static ExecutorService newCachedThreadPool() {
		return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	}

	/**
	 * @see #newCachedThreadPool()
	 */
	public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
		return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
				threadFactory);
	}

	/**
	 * 创建CachedThreadPool，与Executors的默认实现相比, 线程总数依然无上限，但可设置KeepAlive时间(默认1分钟).
	 */
	public static ExecutorService newCachedThreadPool(long keepAliveSecs) {
		return new ThreadPoolExecutor(0, Integer.MAX_VALUE, keepAliveSecs, TimeUnit.SECONDS,
				new SynchronousQueue<Runnable>());
	}

	/**
	 * @see #newCachedThreadPool(long)
	 */
	public static ExecutorService newCachedThreadPool(long keepAliveSecs, ThreadFactory threadFactory) {
		return new ThreadPoolExecutor(0, Integer.MAX_VALUE, keepAliveSecs, TimeUnit.SECONDS,
				new SynchronousQueue<Runnable>(), threadFactory);
	}

	/**
	 * 创建CachedThreadPool，与Executors的默认实现相比, 可设置maxThreads(默认无限)与keepAlive时间(默认1分钟).
	 * 
	 * 当到达maxThreads时，调用RejectHandler，默认为AbortPolicy，抛出RejectedExecutionException异常,
	 * 其他可选的Policy包括静默放弃任务(Discard)或由主线程来直接执行(CallerRuns).
	 */
	public static ExecutorService newCachedThreadPool(int maxThreads, long keepAliveSecs) {
		return new ThreadPoolExecutor(0, maxThreads, keepAliveSecs, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	}

	/**
	 * @see #newCachedThreadPool(int, long)
	 */
	public static ExecutorService newCachedThreadPool(int maxThreads, long keepAliveSecs, ThreadFactory threadFactory) {
		return new ThreadPoolExecutor(0, maxThreads, keepAliveSecs, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
				threadFactory);
	}

	/**
	 * @see #newCachedThreadPool(int, long)
	 */
	public static ExecutorService newCachedThreadPool(int maxThreads, long keepAliveSecs, ThreadFactory threadFactory,
			RejectedExecutionHandler rejectHanlder) {
		return new ThreadPoolExecutor(0, maxThreads, keepAliveSecs, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
				threadFactory, rejectHanlder);
	}

	/**
	 * 保证不会有Exception抛出到线程池的Runnable，防止用户没有捕捉异常导致中断了线程池中的线程。
	 */
	public static class WrapExceptionRunnable implements Runnable {

		private static Logger logger = LoggerFactory.getLogger(WrapExceptionRunnable.class);

		private Runnable runnable;

		public WrapExceptionRunnable(Runnable runnable) {
			this.runnable = runnable;
		}

		@Override
		public void run() {
			try {
				runnable.run();
			} catch (Throwable e) {
				// catch any exception, because the scheduled thread will break if the exception thrown to outside.
				logger.error("Unexpected error occurred in task", e);
			}
		}
	}

}
