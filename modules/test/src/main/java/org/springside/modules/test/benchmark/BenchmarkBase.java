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
		for (int i = 0; i < threadCount; i++) {
			threadPool.execute(getTask());
		}

		//wait for all threads ready
		startLock.await();

		System.out.println("Test start");
		Date startTime = new Date();

		//wait for all threads finish
		finishLock.await();

		long timeInMills = new Date().getTime() - startTime.getTime();
		long invokeTimes = threadCount * loopCount;
		System.out.printf("Thread count is %d, Spend %d ms for %d request, TPS is %d\n", threadCount, timeInMills,
				invokeTimes, (invokeTimes * 1000 / timeInMills));

		threadPool.shutdownNow();

		onFinish();
	}

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

	protected void onThreadFinish(Date threadStartTime) {
		// notify test finish
		finishLock.countDown();

		BigDecimal latency = new BigDecimal((new Date().getTime() - threadStartTime.getTime())).divide(new BigDecimal(
				loopCount), 2, BigDecimal.ROUND_HALF_UP);
		System.out.println("Thread average latency " + latency + "ms");
	}

	protected void onStart() {
	}

	protected void onFinish() {
	}

	abstract protected Runnable getTask();

}
