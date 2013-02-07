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
	protected int printBetweenMills;

	protected long threadStartTime;
	protected long previousPrintTime;
	protected long previousLoop = 0L;

	public BenchmarkTask(int taskSequence, ConcurrentBenchmark parent, int printBetweenSeconds) {
		this.taskSequence = taskSequence;
		this.parent = parent;
		this.printBetweenMills = printBetweenSeconds * 1000;
	}

	/**
	 * Must be invoked when each thread finish the preparation. 
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
		previousPrintTime = threadStartTime;
	}

	/**
	 * Must be invoked when the loop in thread finished.
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

		if ((currentTime - previousPrintTime) > printBetweenMills) {
			long lastTimeInMills = currentTime - previousPrintTime;
			previousPrintTime = currentTime;

			long totalRequest = currentLoop + 1;
			long lastRequests = totalRequest - previousLoop;

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
							taskSequence, totalRequest, totalTimeInSeconds, lastTps, lastLatency.toString(), totalTps,
							totalLatency.toString());

			previousLoop = currentLoop;
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

		System.out.printf("Thread %02d finish.Total tps/latency is %,d/%sms\n", taskSequence, totalTps,
				totalLatency.toString());
	}
}
