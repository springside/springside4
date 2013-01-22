package org.springside.modules.test.benchmark;

import java.math.BigDecimal;
import java.util.Date;

import com.google.common.util.concurrent.RateLimiter;

public abstract class BenchmarkTask implements Runnable {

	protected int threadIndex;

	protected Date startTime;

	protected RateLimiter rateLimiter;
	protected int printInfoInterval; //单位为秒.
	protected long previous = 0L;

	protected BenchmarkBase parent;

	public BenchmarkTask(int threadIndex, BenchmarkBase parent, int printInfoInterval) {
		this.threadIndex = threadIndex;
		this.parent = parent;
		this.printInfoInterval = printInfoInterval;

		this.rateLimiter = RateLimiter.create(1d / printInfoInterval);
	}

	/**
	 * Must be invoked after each thread finish the prepare job, return the startTime. 
	 */
	protected void onThreadStart() {
		parent.startLock.countDown();
		//wait for all other threads ready
		try {
			parent.startLock.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		startTime = new Date();
	}

	/**
	 * Must be invoked after the loop in thread finish.
	 */
	protected void onThreadFinish() {
		// notify test finish
		parent.finishLock.countDown();
		System.out.printf("Thread %02d finish.\n", threadIndex);
	}

	/**
	 * 间隔printInfoInterval的时间打印信息。
	 */
	protected void printInfo(int current) {
		if (rateLimiter.tryAcquire()) {
			String totalTime = new BigDecimal(new Date().getTime() - startTime.getTime()).divide(new BigDecimal(1000),
					0, BigDecimal.ROUND_HALF_UP).toString();
			long requests = (current - previous) + 1;

			long tps = requests / printInfoInterval;
			String latency = new BigDecimal(printInfoInterval * 1000).divide(new BigDecimal(requests), 2,
					BigDecimal.ROUND_HALF_UP).toString();

			System.out.printf(
					"Thread %02d finish %,d requests after %s seconds. Last TPS is %,d and latency is %sms.\n",
					threadIndex, current + 1, totalTime, tps, latency);
			previous = current;
		}
	}
}
