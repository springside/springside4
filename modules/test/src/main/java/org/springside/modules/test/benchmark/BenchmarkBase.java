package org.springside.modules.test.benchmark;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BenchmarkBase {

	protected int threadCount;
	protected long loopCount;

	protected CountDownLatch startLock;
	protected CountDownLatch finishLock;

	public BenchmarkBase(int threadCount, long loopCount) {
		this.threadCount = threadCount;
		this.loopCount = loopCount;

		startLock = new CountDownLatch(threadCount);
		finishLock = new CountDownLatch(threadCount);
	}

	public void run() throws Exception {
		onStart();

		//start threads
		ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);
		try {
			for (int i = 0; i < threadCount; i++) {
				threadPool.execute(getTask(i));
			}

			//wait for all threads ready
			startLock.await();

			//print start message
			String className = this.getClass().getSimpleName();
			long invokeTimes = threadCount * loopCount;
			System.out.printf("%s start. %,d request will be invoked.\n", className, invokeTimes);
			Date startTime = new Date();

			//wait for all threads finish
			finishLock.await();

			//print summary message
			long timeInMills = new Date().getTime() - startTime.getTime();

			System.out.printf("%s finish.\nThread count is %d, spend %,d ms for %,d request, TPS is %,d.\n", className,
					threadCount, timeInMills, invokeTimes, (invokeTimes * 1000 / timeInMills));
		} finally {
			threadPool.shutdownNow();
			onFinish();
		}
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
	abstract protected Runnable getTask(int threadIndex);
}
