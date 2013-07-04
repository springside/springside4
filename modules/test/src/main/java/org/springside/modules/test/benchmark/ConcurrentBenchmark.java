package org.springside.modules.test.benchmark;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 多线程Benchmark测试框架. 提供多线程调度及定时的测试进度, tps/latency信息打印.
 * 为方便移植重用, 无任何第三方类库依赖.
 * 
 * @author calvin
 */
public abstract class ConcurrentBenchmark {

	public static final String THREAD_COUNT_NAME = "benchmark.thread.count";
	public static final String LOOP_COUNT_NAME = "benchmark.loop.count";

	protected int threadCount;
	protected long loopCount;

	protected CountDownLatch startLock;
	protected CountDownLatch finishLock;

	protected Date startTime;
	protected int intervalInMills;

	public ConcurrentBenchmark(int defaultThreadCount, long defaultLoopCount, int intervalInSeconds) {
		// merge default setting and system properties
		this.threadCount = Integer.parseInt(System.getProperty(THREAD_COUNT_NAME, String.valueOf(defaultThreadCount)));
		this.loopCount = Long.parseLong(System.getProperty(LOOP_COUNT_NAME, String.valueOf(defaultLoopCount)));

		startLock = new CountDownLatch(threadCount);
		finishLock = new CountDownLatch(threadCount);

		this.intervalInMills = intervalInSeconds * 1000;
	}

	public void execute() throws Exception {
		// override for connection & data setup
		setUp();

		// start threads
		ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);
		try {
			for (int i = 0; i < threadCount; i++) {
				BenchmarkTask task = createTask(i);
				threadPool.execute(task);
			}

			// wait for all threads ready
			startLock.await();

			// print start message
			startTime = new Date();
			printStartMessage();

			// wait for all threads finish
			finishLock.await();

			// print finish summary message
			printFinishMessage();
		} finally {
			threadPool.shutdownNow();
			// override for connection & data cleanup
			tearDown();
		}
	}

	protected void printStartMessage() {
		String className = this.getClass().getSimpleName();
		long invokeTimes = threadCount * loopCount;

		System.out.printf("%s started at %s.\n%d threads with %,d loops, totally %,d requests will be invoked.\n",
				className, startTime.toString(), threadCount, loopCount, invokeTimes);
	}

	protected void printFinishMessage() {
		Date endTime = new Date();
		String className = this.getClass().getSimpleName();
		long invokeTimes = threadCount * loopCount;
		long timeInMills = endTime.getTime() - startTime.getTime();
		long tps = (invokeTimes * 1000) / timeInMills;

		System.out.printf("%s finished at %s.\n%d threads processed %,d requests after %,d ms, tps is %,d.\n",
				className, endTime.toString(), threadCount, invokeTimes, timeInMills, tps);
	}

	/**
	 * Override to connect resource and prepare global data.
	 */
	protected void setUp() {
	}

	/**
	 * Override to disconnect resource, verify result and cleanup global data .
	 */
	protected void tearDown() {
	}

	/**
	 * Return a new benchmark task.
	 * 
	 * @param taskSequence the sequence number of the task.
	 */
	protected abstract BenchmarkTask createTask(int taskSequence);
}
