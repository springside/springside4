package org.springside.modules.metrics;

import java.util.concurrent.atomic.AtomicLong;

import org.springside.modules.metrics.utils.Clock;

public class Counter {
	public static Clock clock = Clock.DEFAULT;

	private AtomicLong counter = new AtomicLong(0);

	private long lastReportCount = 0L;
	private long lastReportTime;

	public Counter() {
		lastReportTime = clock.getCurrentTime();
	}

	public void inc() {
		counter.incrementAndGet();
	}

	public void inc(long n) {
		counter.addAndGet(n);
	}

	public void dec() {
		counter.decrementAndGet();
	}

	public void dec(long n) {
		counter.addAndGet(-n);
	}

	public long reset() {
		long currentValue = counter.getAndSet(0);
		lastReportCount = 0L;
		lastReportTime = clock.getCurrentTime();
		return currentValue;
	}

	public CounterMetric getMetric() {
		long currentCount = counter.get();
		long currentTime = clock.getCurrentTime();

		CounterMetric metric = new CounterMetric();
		metric.count = currentCount;
		if ((currentTime - lastReportTime) > 0) {
			metric.lastRate = ((currentCount - lastReportCount) * 1000) / ((currentTime - lastReportTime));
		}

		lastReportCount = currentCount;
		lastReportTime = currentTime;

		return metric;
	}

	@Override
	public String toString() {
		return "Counter [counter=" + counter + ", lastReportCount=" + lastReportCount + ", lastReportTime="
				+ lastReportTime + "]";
	}
}
