package org.springside.modules.metrics;

import org.springside.modules.metrics.utils.Clock;

public class Execution {

	public static class ExecutiomTimer {
		private final Execution execution;
		private final Clock clock;
		private final long startTime;

		private ExecutiomTimer(Execution execution, Clock clock) {
			this.execution = execution;
			this.clock = clock;
			this.startTime = clock.getCurrentTime();
		}

		public long stop() {
			final long elapsed = clock.getCurrentTime() - startTime;
			execution.update(elapsed);
			return elapsed;
		}
	}

	private Counter counter;
	private Histogram histogram;
	private Clock clock;

	public Execution() {
		this(Clock.DEFAULT);
	}

	public Execution(Clock clock) {
		this.clock = clock;
	}

	public ExecutiomTimer start() {
		return new ExecutiomTimer(this, clock);
	}

	private void update(long duration) {
		histogram.update(duration);
		counter.inc();
	}

	public HistogramSnapshot getSnapshot() {
		return histogram.getSnapshot();
	}

	public long getCount() {
		return counter.getCount();
	}

	public double getLastRate() {
		return counter.getLastRate();
	}

	public double getMeanRate() {
		return counter.getMeanRate();
	}
}
