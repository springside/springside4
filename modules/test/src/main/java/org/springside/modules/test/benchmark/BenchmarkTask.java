package org.springside.modules.test.benchmark;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark中任务线程的基类.
 * 
 * @see ConcurrentBenchmark
 */
public abstract class BenchmarkTask implements Runnable {

	protected int taskSequence;
	protected ConcurrentBenchmark parent;

	protected long previousRequests = 0L;
	protected long nextTime;

	@Override
	public void run() {
		setUp();
		onThreadStart();
		try {
			for (int i = 1; i <= parent.loopCount; i++) {
				execute(i);
			}
		} finally {
			tearDown();
			onThreadFinish();
		}
	}

	protected void execute(final int requestSequence) {

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
	 * Must be invoked in children class when each thread finish the preparation.
	 */
	protected void onThreadStart() {
		parent.startLock.countDown();
		// wait for all other threads ready
		try {
			parent.startLock.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		nextTime = System.currentTimeMillis() + parent.intervalInMills;

	}

	/**
	 * Must be invoked in children class when each thread finish loop.
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
	protected void printProgressMessage(final int currentRequests) {
		long currentTime = System.currentTimeMillis();

		if (currentTime > nextTime) {
			long lastIntervalInMills = parent.intervalInMills + (currentTime - nextTime);
			nextTime = currentTime + parent.intervalInMills;

			long lastRequests = currentRequests - previousRequests;

			long totalTimeInMills = currentTime - parent.startTime.getTime();
			long totalTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(totalTimeInMills);

			long totalTps = (currentRequests * 1000) / totalTimeInMills;
			long lastTps = (lastRequests * 1000) / lastIntervalInMills;

			BigDecimal lastLatency = new BigDecimal(lastIntervalInMills).divide(new BigDecimal(lastRequests), 2,
					BigDecimal.ROUND_HALF_UP);
			BigDecimal totalLatency = new BigDecimal(totalTimeInMills).divide(new BigDecimal(currentRequests), 2,
					BigDecimal.ROUND_HALF_UP);

			System.out
					.printf("Thread %02d process %,d requests after %s seconds. Last tps/latency is %,d/%sms. Total tps/latency is %,d/%sms.\n",
							taskSequence, currentRequests, totalTimeInSeconds, lastTps, lastLatency.toString(),
							totalTps, totalLatency.toString());

			previousRequests = currentRequests;
		}
	}

	/**
	 * 打印线程结果信息.
	 */
	protected void printThreadFinishMessage() {
		long totalTimeInMills = System.currentTimeMillis() - parent.startTime.getTime();
		long totalRequest = parent.loopCount;
		long totalTps = (totalRequest * 1000) / totalTimeInMills;
		BigDecimal totalLatency = new BigDecimal(totalTimeInMills).divide(new BigDecimal(totalRequest), 2,
				BigDecimal.ROUND_HALF_UP);

		System.out.printf("Thread %02d finish.Total tps/latency is %,d/%sms\n", taskSequence, totalTps,
				totalLatency.toString());
	}
}
