package org.springside.modules.test.benchmark;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark中任务线程的基类.
 * 
 * @see ConcurrentBenchmark 
 */
public abstract class BenchmarkTask implements Runnable {

	protected int threadIndex;
	protected ConcurrentBenchmark parent;
	protected int printBetweenMills;

	protected long threadStartTime;
	protected long previousTime;
	protected long previous = 0L;

	public BenchmarkTask(int threadIndex, ConcurrentBenchmark parent, int printBetweenSeconds) {
		this.threadIndex = threadIndex;
		this.parent = parent;
		this.printBetweenMills = printBetweenSeconds * 1000;
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
		threadStartTime = System.currentTimeMillis();
		previousTime = threadStartTime;
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
	protected void printProgressMessage(int currentLoop) {
		long currentTime = System.currentTimeMillis();

		if ((currentTime - previousTime) > printBetweenMills) {
			long lastTimeInMills = currentTime - previousTime;
			previousTime = currentTime;

			long totalRequest = currentLoop + 1;
			long lastRequests = totalRequest - previous;

			long totalTimeInMills = currentTime - threadStartTime;
			long totalTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(totalTimeInMills);

			long totalTps = totalRequest * 1000 / totalTimeInMills;
			long lastTps = lastRequests * 1000 / lastTimeInMills;

			BigDecimal lastLatency = new BigDecimal(lastTimeInMills).divide(new BigDecimal(lastRequests), 2,
					BigDecimal.ROUND_HALF_UP);
			BigDecimal totalLatency = new BigDecimal(totalTimeInMills).divide(new BigDecimal(totalRequest), 2,
					BigDecimal.ROUND_HALF_UP);

			System.out
					.printf("Thread %02d process %,d requests after %s seconds. Last tps/latency is %,d/%sms. Total tps/latency is %,d/%sms.\n",
							threadIndex, totalRequest, totalTimeInSeconds, lastTps, lastLatency.toString(), totalTps,
							totalLatency.toString());

			previous = currentLoop;
		}
	}

	/**
	 * 打印线程结果信息.
	 */
	protected void printThreadFinishMessage() {
		long totalTimeInMills = System.currentTimeMillis() - threadStartTime;
		long totalRequest = parent.loopCount;
		long totalTps = totalRequest * 1000 / totalTimeInMills;
		BigDecimal totalLatency = new BigDecimal(totalTimeInMills).divide(new BigDecimal(totalRequest), 2,
				BigDecimal.ROUND_HALF_UP);

		System.out.printf("Thread %02d finish.Total tps/latency is %,d/%sms\n", threadIndex, totalTps,
				totalLatency.toString());
	}
}
