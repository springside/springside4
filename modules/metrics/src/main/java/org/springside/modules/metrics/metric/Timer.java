/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics.metric;

import org.springside.modules.metrics.utils.Clock;

/**
 * Timer类型，兼具Counter和Histogram的简便写法.
 * 
 * 有两种用法： 
 * 
 * 1. 使用timerContext
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
 */
public class Timer {

	public static Clock clock = Clock.DEFAULT;

	public TimerMetric latestMetric; // snapshot值

	private Counter counter;
	private Histogram histogram;

	public Timer(Double... pcts) {
		this(1, pcts);
	}

	public Timer(Integer sampleRate, Double... pcts) {
		counter = new Counter();
		histogram = new Histogram(sampleRate, pcts);
		latestMetric = new TimerMetric();
	}

	//使用方法1
	public TimerContext start() {
		return new TimerContext(this, clock.getCurrentTime());
	}
	
	//使用方法2
	public void update(long start) {
		histogram.update(clock.getCurrentTime() - start);
		counter.inc();
	}

	/**
	 * 计算单位时间内的metrics值, 存入该Metrics的Snapshot中，并清零原始数据.
	 */
	public TimerMetric calculateMetric() {
		TimerMetric metric = new TimerMetric();
		metric.counterMetric = counter.calculateMetric();
		metric.histogramMetric = histogram.calculateMetric();
		latestMetric = metric;
		return metric;
	}
	
	/**
	 * 重设counter与histogram
	 */
	public void reset(){
		counter.reset();
		histogram.reset();
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
			timer.update(startTime);
		}
	}
}
