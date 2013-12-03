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
		return new ExecutionTimer(this, clock);
	}

	private void update(long duration) {
		histogram.update(duration);
		counter.inc();
	}

	public ExecutionMetric getMetric() {
		ExecutionMetric metric = new ExecutionMetric();
		metric.counter = counter.getMetric();
		metric.histogram = histogram.getMetric();
		return metric;
	}

	public static class ExecutionTimer {
		private final Execution execution;
		private final Clock clock;
		private final long startTime;

		private ExecutionTimer(Execution execution, Clock clock) {
			this.execution = execution;
			this.clock = clock;
			this.startTime = clock.getCurrentTime();
		}

		public void stop() {
			final long elapsed = clock.getCurrentTime() - startTime;
			execution.update(elapsed);
		}
	}

	@Override
	public String toString() {
		return "Execution [counter=" + counter + ", histogram=" + histogram + "]";
	}
}
