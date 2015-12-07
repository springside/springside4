package org.springside.modules.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPool创建的工具类.
 * 
 * 对比JDK Executors中的newFixedThreadPool()和newCachedThreadPool()函数，提供更多有用的配置项.
 * 
 * 使用示例：
 * 
 * <pre>
 * ExecutorService ExecutorService = new FixedThreadPoolBuilder().setPoolSize(10).build();
 * </pre>
 */
public class ThreadPoolBuilder {

	private static RejectedExecutionHandler defaultRejectHandler = new AbortPolicy();

	/**
	 * 创建FixedThreadPool.
	 * 
	 * 1. 任务提交时, 如果线程数还没达到poolSize即创建新线程并绑定任务(即poolSize次提交后线程总数必达到poolSize，不会重用之前的线程)
	 * 
	 * 1.a poolSize是必填项，不能忽略.
	 * 
	 * 2. 第poolSize次任务提交后, 新增任务放入Queue中, Pool中的所有线程从Queue中take任务执行.
	 * 
	 * 2.a Queue默认为无限长的LinkedBlockingQueue, 也可以设置queueSize换成有界的队列.
	 * 
	 * 2.b 如果使用有界队列, 当队列满了之后,会调用RejectHandler进行处理, 默认为AbortPolicy，抛出RejectedExecutionException异常.
	 * 其他可选的Policy包括静默放弃当前任务(Discard)，放弃Queue里最老的任务(DisacardOldest)，或由主线程来直接执行(CallerRuns).
	 * 
	 * 3. 因为线程全部为core线程，所以不会在空闲回收.
	 */
	public static class FixedThreadPoolBuilder {

		private int poolSize = 0;
		private int queueSize = 0;

		private ThreadFactory threadFactory = null;
		private RejectedExecutionHandler rejectHandler;

		public FixedThreadPoolBuilder setPoolSize(int poolSize) {
			this.poolSize = poolSize;
			return this;
		}

		public FixedThreadPoolBuilder setQueueSize(int queueSize) {
			this.queueSize = queueSize;
			return this;
		}

		public FixedThreadPoolBuilder setThreadFactory(ThreadFactory threadFactory) {
			this.threadFactory = threadFactory;
			return this;
		}

		public FixedThreadPoolBuilder setRejectHanlder(RejectedExecutionHandler rejectHandler) {
			this.rejectHandler = rejectHandler;
			return this;
		}

		public ExecutorService build() {
			if (poolSize < 1) {
				throw new IllegalArgumentException("size not set");
			}

			BlockingQueue<Runnable> queue = null;
			if (queueSize == 0) {
				queue = new LinkedBlockingQueue<Runnable>();
			} else {
				queue = new ArrayBlockingQueue<Runnable>(queueSize);
			}

			if (threadFactory == null) {
				threadFactory = Executors.defaultThreadFactory();
			}

			if (rejectHandler == null) {
				rejectHandler = defaultRejectHandler;
			}

			return new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, queue, threadFactory,
					rejectHandler);
		}
	}

	/**
	 * 创建CachedThreadPool.
	 * 
	 * 1. 任务提交时, 如果线程数还没达到minSize即创建新线程并绑定任务(即minSize次提交后线程总数必达到minSize, 不会重用之前的线程)
	 * 
	 * 1.a minSize默认为0, 可设置保证有基本的线程处理请求不被回收.
	 * 
	 * 2. 第minSize次任务提交后, 新增任务提交进SynchronousQueue后，如果没有空闲线程立刻处理，则会创建新的线程, 直到总线程数达到上限.
	 * 
	 * 2.a maxSize默认为Integer.Max, 可进行设置.
	 * 
	 * 2.b 如果设置了maxSize, 当总线程数达到上限, 会调用RejectHandler进行处理, 默认为AbortPolicy, 抛出RejectedExecutionException异常.
	 * 其他可选的Policy包括静默放弃当前任务(Discard)，或由主线程来直接执行(CallerRuns).
	 * 
	 * 3. minSize以上, maxSize以下的线程, 如果在keepAliveTime中都poll不到任务执行将会被结束掉, keeAliveTime默认为60秒, 可设置.
	 */
	public static class CachedThreadPoolBuilder {

		private int minSize = 0;
		private int maxSize = Integer.MAX_VALUE;
		private int keepAliveSecs = 60;

		private ThreadFactory threadFactory = null;
		private RejectedExecutionHandler rejectHandler;

		public CachedThreadPoolBuilder setMinSize(int minSize) {
			this.minSize = minSize;
			return this;
		}

		public CachedThreadPoolBuilder setMaxSize(int maxSize) {
			this.maxSize = maxSize;
			return this;
		}

		public CachedThreadPoolBuilder setKeepAliveSecs(int keepAliveSecs) {
			this.keepAliveSecs = keepAliveSecs;
			return this;
		}

		public CachedThreadPoolBuilder setThreadFactory(ThreadFactory threadFactory) {
			this.threadFactory = threadFactory;
			return this;
		}

		public CachedThreadPoolBuilder setRejectHanlder(RejectedExecutionHandler rejectHandler) {
			this.rejectHandler = rejectHandler;
			return this;
		}

		public ExecutorService build() {

			if (threadFactory == null) {
				threadFactory = Executors.defaultThreadFactory();
			}

			if (rejectHandler == null) {
				rejectHandler = defaultRejectHandler;
			}

			return new ThreadPoolExecutor(minSize, maxSize, keepAliveSecs, TimeUnit.SECONDS,
					new SynchronousQueue<Runnable>(), threadFactory, rejectHandler);
		}
	}

	/**
	 * 可同时设置min/max/queue Size的线程池, 仅用于特殊场景.
	 * 
	 * 比如并发要求非常高，觉得SynchronousQueue的性能太差.
	 * 
	 * 比如平常使用Core线程工作，如果满了先放queue，queue满再开临时线程，此时queue的长度一定要按项目需求设好.
	 */
	public static class ConfigurableThreadPoolBuilder {

		private int minSize = 0;
		private int maxSize = Integer.MAX_VALUE;
		private int queueSize = 0;
		private int keepAliveSecs = 60;

		private ThreadFactory threadFactory = null;
		private RejectedExecutionHandler rejectHandler;

		public ConfigurableThreadPoolBuilder setMinSize(int minSize) {
			this.minSize = minSize;
			return this;
		}

		public ConfigurableThreadPoolBuilder setMaxSize(int maxSize) {
			this.maxSize = maxSize;
			return this;
		}

		public ConfigurableThreadPoolBuilder setQueueSize(int queueSize) {
			this.queueSize = queueSize;
			return this;
		}

		public ConfigurableThreadPoolBuilder setKeepAliveSecs(int keepAliveSecs) {
			this.keepAliveSecs = keepAliveSecs;
			return this;
		}

		public ConfigurableThreadPoolBuilder setThreadFactory(ThreadFactory threadFactory) {
			this.threadFactory = threadFactory;
			return this;
		}

		public ConfigurableThreadPoolBuilder setRejectHanlder(RejectedExecutionHandler rejectHandler) {
			this.rejectHandler = rejectHandler;
			return this;
		}

		public ExecutorService build() {

			BlockingQueue<Runnable> queue = null;
			if (queueSize == 0) {
				queue = new LinkedBlockingQueue<Runnable>();
			} else {
				queue = new ArrayBlockingQueue<Runnable>(queueSize);
			}

			if (threadFactory == null) {
				threadFactory = Executors.defaultThreadFactory();
			}

			if (rejectHandler == null) {
				rejectHandler = defaultRejectHandler;
			}

			return new ThreadPoolExecutor(minSize, maxSize, keepAliveSecs, TimeUnit.SECONDS, queue, threadFactory,
					rejectHandler);
		}
	}

}
