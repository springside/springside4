package org.springside.modules.metrics;

import org.springside.modules.metrics.utils.Clock;

public class Execution {
	private Counter counter;
	private Histogram histogram;
	private Clock clock;

	public Execution(Clock clock, Double[] pcts) {
		if (clock == null) {
			throw new IllegalArgumentException("Clock can't be null ");
		}
		this.clock = clock;
		counter = new Counter(clock);
		histogram = new Histogram(pcts);
	}

	public ExecutionTimer start() {
		return new ExecutionTimer(this, clock.getCurrentTime());
	}

	private void update(long startTime) {
		final long elapsed = clock.getCurrentTime() - startTime;
		histogram.update(elapsed);
		counter.inc();
	}

	public ExecutionMetric getMetric() {
		ExecutionMetric metric = new ExecutionMetric();
		metric.counter = counter.getMetric();
		metric.histogram = histogram.getMetric();
		return metric;
	}

	@Override
	public String toString() {
		return "Execution [counter=" + counter + ", histogram=" + histogram + "]";
	}

	public static class ExecutionTimer {
		private final Execution execution;
		private final long startTime;

		private ExecutionTimer(Execution execution, long startTime) {
			this.execution = execution;
			this.startTime = startTime;
		}

		public void stop() {
			execution.update(startTime);
		}
	}
}
