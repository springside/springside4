package org.springside.modules.metrics;

import org.springside.modules.metrics.utils.Clock;

public class Execution {
	private Counter counter;
	private Histogram histogram;
	private Clock clock;

	public Execution(Clock clock, Double[] pcts) {
		this.clock = clock;
		counter = new Counter(clock);
		histogram = new Histogram(pcts);
	}

	public ExecutiomTimer start() {
		return new ExecutiomTimer(this, clock);
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

	public static class ExecutiomTimer {
		private final Execution execution;
		private final Clock clock;
		private final long startTime;

		private ExecutiomTimer(Execution execution, Clock clock) {
			this.execution = execution;
			this.clock = clock;
			this.startTime = clock.getCurrentTime();
		}

		public void stop() {
			final long elapsed = clock.getCurrentTime() - startTime;
			execution.update(elapsed);
		}
	}
}
