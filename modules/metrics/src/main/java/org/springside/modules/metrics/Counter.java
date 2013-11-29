package org.springside.modules.metrics;

import java.util.concurrent.atomic.AtomicLong;

import org.springside.modules.metrics.utils.Clock;

public class Counter {

	private AtomicLong counter = new AtomicLong(0);
	private long lastReportCounter = 0L;

	private Clock clock;
	private long startTime;
	private long lastReportTime;

	public Counter() {
		this(Clock.DEFAULT);
	}

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
		return counter.getAndSet(0);
	}

	public long getCount() {
		return counter.get();
	}

	public double getLastRate() {
		long currentCounter = counter.get();
		long reportTime = clock.getCurrentTime();

		double rate = 0.0;
		if (currentCounter != 0) {
			rate = ((currentCounter - lastReportCounter) / (reportTime - lastReportTime)) * 1000;
		}
		lastReportCounter = currentCounter;
		lastReportTime = reportTime;

		return rate;
	}

	public double getMeanRate() {
		if (counter.get() == 0) {
			return 0.0;
		} else {
			return (counter.get() / (clock.getCurrentTime() - startTime)) * 1000;
		}
	}
}
