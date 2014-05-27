/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics;

import org.springside.modules.metrics.utils.Clock;

/**
 * Execution类型，兼具Counter和Histogram的简便写法.
 * 
 * 有两种用法：
 * 1. 使用timer
 * 
 * <pre>
 * ExecutionTimer timer = execution.start();
 * ...
 * timer.stop();
 * </pre>
 * 
 * 2. 自行计算，更吝啬一点
 * 
 * <pre>
 * long start = System.currentTimeMillis();
 * ....
 * execution.update(start);
 * </pre>
 * 
 * 
 * @author Calvin
 */
public class Execution {
	public static Clock clock = Clock.DEFAULT;
	private Counter counter;
	private Histogram histogram;

	public Execution(Double... pcts) {
		counter = new Counter();
		histogram = new Histogram(pcts);
	}

	public void update(long start) {
		histogram.update(System.currentTimeMillis() - start);
		counter.inc();
	}

	public ExecutionTimer start() {
		return new ExecutionTimer(this, clock.getCurrentTime());
	}

	private void stopTimer(long startTime) {
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

	/**
	 * 保存某一次请求的初始时间与Execution实例.
	 */
	public static class ExecutionTimer {
		private final Execution execution;
		private final long startTime;

		private ExecutionTimer(Execution execution, long startTime) {
			this.execution = execution;
			this.startTime = startTime;
		}

		public void stop() {
			execution.stopTimer(startTime);
		}
	}
}
