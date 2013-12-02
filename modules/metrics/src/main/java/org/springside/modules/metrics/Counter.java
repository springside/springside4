package org.springside.modules.metrics;

import java.util.concurrent.atomic.AtomicLong;

import org.springside.modules.metrics.utils.Clock;

public class Counter {

	private AtomicLong counter = new AtomicLong(0);
	private long lastReportCount = 0L;

	private Clock clock;
	private long startTime;
	private long lastReportTime;

	public Counter(Clock clock) {
		this.clock = clock;
		startTime = clock.getCurrentTime();
		lastReportTime = startTime;
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
		startTime = clock.getCurrentTime();
		lastReportTime = startTime;
		return currentValue;
	}

	public CounterMetric getMetric() {
		long currentCount = counter.get();
		long currentTime = clock.getCurrentTime();

		CounterMetric metric = new CounterMetric();
		metric.count = currentCount;
		metric.lastRate = ((currentCount - lastReportCount) * 1000) / (currentTime - lastReportTime);
		metric.meanRate = ((currentCount * 1000) / (currentTime - startTime));

		lastReportCount = currentCount;
		lastReportTime = currentTime;

		return metric;
	}
}
