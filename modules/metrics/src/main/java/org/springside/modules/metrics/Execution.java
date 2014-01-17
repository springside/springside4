/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics;

import org.springside.modules.metrics.utils.Clock;

public class Execution {
	public static Clock clock = Clock.DEFAULT;
	private Counter counter;
	private Histogram histogram;

	public Execution(Double... pcts) {
		counter = new Counter();
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

	public ExecutionMetric calculateMetric() {
		ExecutionMetric metric = new ExecutionMetric();
		metric.counterMetric = counter.calculateMetric();
		metric.histogramMetric = histogram.calculateMetric();
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
