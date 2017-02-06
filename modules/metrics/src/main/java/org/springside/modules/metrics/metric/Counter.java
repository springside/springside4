/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics.metric;

import java.util.concurrent.atomic.AtomicLong;

import org.springside.modules.metrics.utils.Clock;

/**
 * Counter类型, 主要用于计算QPS.
 * 
 * 报告Report间隔时间内的Counter变化值及平均QPS，以及服务从启动到现在的Counter总值和平均QPS.
 */
public class Counter {
	public static Clock clock = Clock.DEFAULT;

	public CounterMetric latestMetric; // Snapshot值

	private AtomicLong counter = new AtomicLong(0); // 统计周期内的计数器

	private long startTime; // 启动时间
	private long lastReportTime; // 上一次报告的时间
	private long totalCount; // 从启动到上一次报告周期的总调用次数

	public Counter() {
		reset();
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

	/**
	 * 计算单位时间内的metrics值, 存入该Metrics的Snapshot中，并清零原始数据.
	 */
	public CounterMetric calculateMetric() {
		long latestCount = counter.getAndSet(0);
		long currentTime = clock.getCurrentTime();

		CounterMetric metric = new CounterMetric();

		totalCount += latestCount;
		long totalElapsed = currentTime - startTime;
		metric.avgRate = (totalCount * 1000) / totalElapsed;

		metric.latestCount = latestCount;
		metric.totalCount = totalCount;

		long elapsed = currentTime - lastReportTime;
		if (elapsed > 0) {
			metric.latestRate = (latestCount * 1000) / elapsed;
		}

		lastReportTime = currentTime;

		latestMetric = metric;

		return metric;
	}

	/**
	 * 重设counter，可清除历史的totalCount和avgRate
	 */
	public void reset() {
		latestMetric = new CounterMetric();
		counter.set(0);
		totalCount = 0L;
		startTime = clock.getCurrentTime();
		lastReportTime = startTime;
	}

	@Override
	public String toString() {
		return "Counter [latestMetric=" + latestMetric + ", counter=" + counter + ", totalCount=" + totalCount
				+ ", startTime=" + startTime + ", lastReportTime=" + lastReportTime + "]";
	}
}
