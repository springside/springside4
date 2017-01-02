package org.springside.modules.utils.concurrent.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

import org.springside.modules.utils.concurrent.Threads;
import org.springside.modules.utils.concurrent.threadpool.QueableCachedThreadPool.ControllableQueue;

/**
 * ThreadPool创建的工具类.
 * 
 * 对比JDK Executors中的newFixedThreadPool(), newCachedThreadPool(),newScheduledThreadPool, 提供更多有用的配置项.
 * 
 * 另包含了移植自Tomcat的QueuableCachedPool.
 * 
 * 使用示例如下：
 * 
 * <pre>
 * ExecutorService ExecutorService = new FixedThreadPoolBuilder().setPoolSize(10).build();
 * </pre>
 * 
 * 参考文章 《Java ThreadPool的正确打开方式》http://calvin1978.blogcn.com/articles/java-threadpool.html
 */
public class ThreadPoolBuilder {

	private static RejectedExecutionHandler defaultRejectHandler = new AbortPolicy();

	/**
	 * @see FixedThreadPoolBuilder
	 */
	public static FixedThreadPoolBuilder fixedPool() {
		return new FixedThreadPoolBuilder();
	}

	/**
	 * @see CacheedThreadPoolBuilder
	 */
	public static CachedThreadPoolBuilder cachedPool() {
		return new CachedThreadPoolBuilder();
	}

	/**
	 * @see ScheduledThreadPoolBuilder
	 */
	public static ScheduledThreadPoolBuilder scheduledPool() {
		return new ScheduledThreadPoolBuilder();
	}

	/**
	 * @see QueableCachedThreadPoolBuilder
	 */
	public static QueableCachedThreadPoolBuilder queableCachedPool() {
		return new QueableCachedThreadPoolBuilder();
	}

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
		private String threadNamePrefix = null;
		private Boolean daemon = null;

		private RejectedExecutionHandler rejectHandler;

		public FixedThreadPoolBuilder setPoolSize(int poolSize) {
			this.poolSize = poolSize;
			return this;
		}

		public FixedThreadPoolBuilder setQueueSize(int queueSize) {
			this.queueSize = queueSize;
			return this;
		}

		/**
		 * 与threadNamePrefix互斥, 优先使用ThreadFactory
		 */
		public FixedThreadPoolBuilder setThreadFactory(ThreadFactory threadFactory) {
			this.threadFactory = threadFactory;
			return this;
		}

		public FixedThreadPoolBuilder setThreadNamePrefix(String threadNamePrefix) {
			this.threadNamePrefix = threadNamePrefix;
			return this;
		}

		public FixedThreadPoolBuilder setdaemon(Boolean daemon) {
			this.daemon = daemon;
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

			threadFactory = createThreadFactory(threadFactory, threadNamePrefix, daemon);

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
	 * 3. minSize以上, maxSize以下的线程, 如果在keepAliveTime中都poll不到任务执行将会被结束掉, keeAliveTimeJDK默认为10秒.
	 * 
	 * JDK默认值60秒太高，如高达1000线程时，要低于16QPS时才会开始回收线程, 因此改为默认10秒.
	 */
	public static class CachedThreadPoolBuilder {

		private int minSize = 0;
		private int maxSize = Integer.MAX_VALUE;
		private int keepAliveSecs = 10;

		private ThreadFactory threadFactory = null;
		private String threadNamePrefix = null;
		private Boolean daemon = null;

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

		/**
		 * 与threadNamePrefix互斥, 优先使用ThreadFactory
		 */
		public CachedThreadPoolBuilder setThreadFactory(ThreadFactory threadFactory) {
			this.threadFactory = threadFactory;
			return this;
		}

		public CachedThreadPoolBuilder setThreadNamePrefix(String threadNamePrefix) {
			this.threadNamePrefix = threadNamePrefix;
			return this;
		}

		public CachedThreadPoolBuilder setdaemon(Boolean daemon) {
			this.daemon = daemon;
			return this;
		}

		public CachedThreadPoolBuilder setRejectHanlder(RejectedExecutionHandler rejectHandler) {
			this.rejectHandler = rejectHandler;
			return this;
		}

		public ExecutorService build() {

			threadFactory = createThreadFactory(threadFactory, threadNamePrefix, daemon);

			if (rejectHandler == null) {
				rejectHandler = defaultRejectHandler;
			}

			return new ThreadPoolExecutor(minSize, maxSize, keepAliveSecs, TimeUnit.SECONDS,
					new SynchronousQueue<Runnable>(), threadFactory, rejectHandler);
		}
	}

	/*
	 * 创建ScheduledPool.
	 */
	public static class ScheduledThreadPoolBuilder {

		private int poolSize = 1;
		private ThreadFactory threadFactory = null;
		private String threadNamePrefix = null;

		public ScheduledThreadPoolBuilder setPoolSize(int poolSize) {
			this.poolSize = poolSize;
			return this;
		}

		/**
		 * 与threadNamePrefix互斥, 优先使用ThreadFactory
		 */
		public ScheduledThreadPoolBuilder setThreadFactory(ThreadFactory threadFactory) {
			this.threadFactory = threadFactory;
			return this;
		}

		public ScheduledThreadPoolBuilder setThreadNamePrefix(String threadNamePrefix) {
			this.threadNamePrefix = threadNamePrefix;
			return this;
		}

		public ExecutorService build() {
			threadFactory = createThreadFactory(threadFactory, threadNamePrefix, Boolean.TRUE);
			return new ScheduledThreadPoolExecutor(poolSize, threadFactory);
		}
	}

	/**
	 * 从Tomcat移植过来的可扩展可用Queue缓存任务的ThreadPool
	 * 
	 * @see QueableCachedThreadPool
	 */
	public static class QueableCachedThreadPoolBuilder {

		private int minSize = 0;
		private int maxSize = Integer.MAX_VALUE;
		private int keepAliveSecs = 10;
		private int queueSize = 0;

		private ThreadFactory threadFactory = null;
		private String threadNamePrefix = null;
		private Boolean daemon = null;

		private RejectedExecutionHandler rejectHandler;

		public QueableCachedThreadPoolBuilder setMinSize(int minSize) {
			this.minSize = minSize;
			return this;
		}

		public QueableCachedThreadPoolBuilder setMaxSize(int maxSize) {
			this.maxSize = maxSize;
			return this;
		}

		public QueableCachedThreadPoolBuilder setQueueSize(int queueSize) {
			this.queueSize = queueSize;
			return this;
		}

		public QueableCachedThreadPoolBuilder setKeepAliveSecs(int keepAliveSecs) {
			this.keepAliveSecs = keepAliveSecs;
			return this;
		}

		/**
		 * 与threadNamePrefix互斥, 优先使用ThreadFactory
		 */
		public QueableCachedThreadPoolBuilder setThreadFactory(ThreadFactory threadFactory) {
			this.threadFactory = threadFactory;
			return this;
		}

		public QueableCachedThreadPoolBuilder setThreadNamePrefix(String threadNamePrefix) {
			this.threadNamePrefix = threadNamePrefix;
			return this;
		}

		public QueableCachedThreadPoolBuilder setdaemon(Boolean daemon) {
			this.daemon = daemon;
			return this;
		}

		public QueableCachedThreadPoolBuilder setRejectHanlder(RejectedExecutionHandler rejectHandler) {
			this.rejectHandler = rejectHandler;
			return this;
		}

		public QueableCachedThreadPool build() {

			threadFactory = createThreadFactory(threadFactory, threadNamePrefix, daemon);

			if (rejectHandler == null) {
				rejectHandler = defaultRejectHandler;
			}

			return new QueableCachedThreadPool(minSize, maxSize, keepAliveSecs, TimeUnit.SECONDS,
					new ControllableQueue(queueSize), threadFactory, rejectHandler);
		}
	}

	private static ThreadFactory createThreadFactory(ThreadFactory threadFactory, String threadNamePrefix,
			Boolean daemon) {
		if (threadFactory != null) {
			return threadFactory;
		}

		if (threadNamePrefix != null) {
			return Threads.buildThreadFactory(threadNamePrefix, daemon);
		}

		return Executors.defaultThreadFactory();
	}
}
