package org.springside.modules.test.benchmark;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 多线程Benchmark测试框架.
 * 使用JDK Concurrency中的CountDownLatch精确控制多线程中的测试任务启动与停止及计时.
 * 使用Guava RateLimiter 间隔固定时间打印测试进度及性能数据.
 * 
 * @author calvin
 */
public abstract class ConcurrentBenchmark {

	protected int threadCount;
	protected long loopCount;

	protected CountDownLatch startLock;
	protected CountDownLatch finishLock;

	protected Date startTime;

	public ConcurrentBenchmark(int threadCount, long loopCount) {
		this.threadCount = threadCount;
		this.loopCount = loopCount;

		startLock = new CountDownLatch(threadCount);
		finishLock = new CountDownLatch(threadCount);
	}

	public void run() throws Exception {
		//override for data prepare
		onStart();

		//start threads
		ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);
		try {
			for (int i = 0; i < threadCount; i++) {
				threadPool.execute(createTask(i));
			}

			//wait for all threads ready
			startLock.await();

			//print start message
			startTime = new Date();
			printStartMessage();

			//wait for all threads finish
			finishLock.await();

			//print finish summary message
			printFinishMessage();
		} finally {
			threadPool.shutdownNow();
			//override for data cleanup
			onFinish();
		}
	}

	protected void printStartMessage() {
		String className = this.getClass().getSimpleName();
		long invokeTimes = threadCount * loopCount;

		System.out.printf("%s start at %s.\n%d threads with %,d loops, total %,d requests will be invoked.\n",
				className, startTime.toString(), threadCount, loopCount, invokeTimes);
	}

	protected void printFinishMessage() {
		Date endTime = new Date();
		String className = this.getClass().getSimpleName();
		long invokeTimes = threadCount * loopCount;
		long timeInMills = endTime.getTime() - startTime.getTime();
		long tps = invokeTimes * 1000 / timeInMills;

		System.out.printf("%s finish at %s.\n%d threads process %,d requests after %,d ms, TPS is %,d.\n", className,
				endTime.toString(), threadCount, invokeTimes, timeInMills, tps);
	}

	/**
	 * Override to do some global data prepare job.
	 */
	protected void onStart() {
	}

	/**
	 * Override to do some global data cleanup and verify job.
	 */
	protected void onFinish() {
	}

	/**
	 * Return a new benchmark task. 
	 */
	abstract protected BenchmarkTask createTask(int threadIndex);
}
