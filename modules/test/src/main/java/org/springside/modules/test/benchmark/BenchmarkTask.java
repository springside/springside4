package org.springside.modules.test.benchmark;

import java.math.BigDecimal;
import java.util.Date;

import com.google.common.util.concurrent.RateLimiter;

/**
 * Beanchmark中任务线程的基类.
 * 
 * @see ConcurrentBenchmark 
 */
public abstract class BenchmarkTask implements Runnable {

	protected int threadIndex;
	protected ConcurrentBenchmark parent;
	protected int printBetweenSeconds;

	protected RateLimiter rateLimiter;
	protected Date threadStartTime;
	protected long previous = 0L;

	public BenchmarkTask(int threadIndex, ConcurrentBenchmark parent, int printBetweenSeconds) {
		this.threadIndex = threadIndex;
		this.parent = parent;
		this.printBetweenSeconds = printBetweenSeconds;

		this.rateLimiter = RateLimiter.create(1d / printBetweenSeconds);
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
		threadStartTime = new Date();
	}

	/**
	 * Must be invoked after the loop in thread finish.
	 */
	protected void onThreadFinish() {
		// notify test finish
		parent.finishLock.countDown();

		// print finish summary message
		printThreadFinishMessage();
	}

	/**
	 * 间隔固定时间打印进度信息.
	 */
	protected void printProgressMessage(int current) {
		if (rateLimiter.tryAcquire()) {
			long totalRequest = current + 1;
			long lastRequests = totalRequest - previous;

			long totalTimeInMills = new Date().getTime() - threadStartTime.getTime();
			totalTimeInMills = totalTimeInMills == 0 ? 1 : totalTimeInMills;
			String totalTimeInSeconds = new BigDecimal(totalTimeInMills).divide(new BigDecimal(1000), 0,
					BigDecimal.ROUND_HALF_UP).toString();

			long totalTps = totalRequest * 1000 / totalTimeInMills;
			long lastTps = lastRequests / printBetweenSeconds;

			BigDecimal lastLatency = new BigDecimal(printBetweenSeconds * 1000).divide(new BigDecimal(lastRequests), 2,
					BigDecimal.ROUND_HALF_UP);
			BigDecimal totalLatency = new BigDecimal(totalTimeInMills).divide(new BigDecimal(totalRequest), 2,
					BigDecimal.ROUND_HALF_UP);

			System.out
					.printf("Thread %02d process %,d requests after %s seconds. Last Tps/latency is %,d/%sms, total Tps/latency is %,d/%sms.\n",
							threadIndex, totalRequest, totalTimeInSeconds, lastTps, lastLatency.toString(), totalTps,
							totalLatency.toString());
			previous = current;
		}
	}

	/**
	 * 打印线程结果信息.
	 */
	protected void printThreadFinishMessage() {
		long totalTimeInMills = new Date().getTime() - threadStartTime.getTime();
		long totalRequest = parent.loopCount;
		long totalTps = totalRequest * 1000 / totalTimeInMills;
		BigDecimal totalLatency = new BigDecimal(totalTimeInMills).divide(new BigDecimal(totalRequest), 2,
				BigDecimal.ROUND_HALF_UP);

		System.out.printf("Thread %02d finish.Total Tps/latency is %,d/%sms\n", threadIndex, totalTps,
				totalLatency.toString());
	}
}
