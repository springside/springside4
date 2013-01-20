package org.springside.modules.test.benchmark;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BenchmarkBase {

	protected int threadCount;
	protected int loopCount;

	protected CountDownLatch startLock;
	protected CountDownLatch finishLock;

	public BenchmarkBase(int threadCount, int loopCount) {
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

			System.out.println(this.getClass().getSimpleName() + " start");
			Date startTime = new Date();

			//wait for all threads finish
			finishLock.await();

			long timeInMills = new Date().getTime() - startTime.getTime();
			long invokeTimes = threadCount * loopCount;
			System.out.printf("%s finish.\nThread count is %d, spend %,d ms for %,d request, TPS is %,d.\n", this
					.getClass().getSimpleName(), threadCount, timeInMills, invokeTimes,
					(invokeTimes * 1000 / timeInMills));
		} finally {
			threadPool.shutdownNow();
		}

		onFinish();
	}

	/**
	 * Must be invoked after each thread finish the prepare job, return the startTime. 
	 */
	protected Date onThreadStart() {
		startLock.countDown();
		//wait for all threads ready
		try {
			startLock.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	/**
	 * Must be invoked after the loop finish.
	 */
	protected void onThreadFinish(Date threadStartTime) {
		// notify test finish
		finishLock.countDown();
		// print result
		BigDecimal latency = new BigDecimal((new Date().getTime() - threadStartTime.getTime())).divide(new BigDecimal(
				loopCount), 2, BigDecimal.ROUND_HALF_UP);
		System.out.println("Thread average latency " + latency + "ms");
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
	abstract protected Runnable getTask(int index);
}
