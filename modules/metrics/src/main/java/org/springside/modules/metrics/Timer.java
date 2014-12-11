/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics;

import org.springside.modules.metrics.utils.Clock;

/**
 * Timer类型，兼具Counter和Histogram的简便写法.
 * 
 * 有两种用法：
 * 1. 使用timer
 * 
 * <pre>
 * TimerContext timerContext = timer.start();
 * ...
 * timerContext.stop();
 * </pre>
 * 
 * 2. 自行计算，更吝啬一点
 * 
 * <pre>
 * long start = System.currentTimeMillis();
 * ....
 * timer.update(start);
 * </pre>
 * 
 * 
 * @author Calvin
 */
public class Timer {

	public static Clock clock = Clock.DEFAULT;

	public TimerMetric latestMetric = new TimerMetric();

	private Counter counter;
	private Histogram histogram;

	public Timer(Double... pcts) {
		counter = new Counter();
		histogram = new Histogram(pcts);
	}

	public void update(long start) {
		histogram.update(System.currentTimeMillis() - start);
		counter.inc();
	}

	public TimerContext start() {
		return new TimerContext(this, clock.getCurrentTime());
	}

	private void stopTimer(long startTime) {
		final long elapsed = clock.getCurrentTime() - startTime;
		histogram.update(elapsed);
		counter.inc();
	}

	public TimerMetric calculateMetric() {
		TimerMetric metric = new TimerMetric();
		metric.counterMetric = counter.calculateMetric();
		metric.histogramMetric = histogram.calculateMetric();
		latestMetric = metric;
		return metric;
	}

	@Override
	public String toString() {
		return "Timer [latestMetric=" + latestMetric + ", counter=" + counter + ", histogram=" + histogram + "]";
	}

	/**
	 * 保存某一次请求的初始时间与Timer实例.
	 */
	public static class TimerContext {
		private final Timer timer;
		private final long startTime;

		private TimerContext(Timer timer, long startTime) {
			this.timer = timer;
			this.startTime = startTime;
		}

		public void stop() {
			timer.stopTimer(startTime);
		}
	}
}
