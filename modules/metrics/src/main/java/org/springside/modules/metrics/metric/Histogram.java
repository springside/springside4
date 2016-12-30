/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Histogram数据类型, 主要用于计算Latency.
 * 
 * 报告Report间隔时间内数值的最小/最大，平均值，以及某个百分比的请求数都小于的值.
 */
public class Histogram {

	public HistogramMetric latestMetric;// Snapshot值

	private Double[] pcts; // 配置需要计算的百分位，如99, 99.99

	private int sampleRate; // 采样率，1代表100%， 2代表50%， 10 代表10%
	private AtomicInteger sampleCounter = new AtomicInteger(0); // 采样率取模用的计数器

	private volatile LinkedList<Long> measurements; // 统计周期内的原始数据

	/**
	 * @param pcts 设定百分位数，可选值如99, 99.99.
	 */
	public Histogram(Double... pcts) {
		this(1, pcts);
	}

	/**
	 * @param sampleRate 采样率，1代表100%， 2代表50%， 10 代表10%
	 * @param pcts 百分位数，可选值如99, 99.99.
	 */
	public Histogram(Integer sampleRate, Double... pcts) {
		this.sampleRate = sampleRate;
		this.pcts = pcts;
		reset();
	}

	public void update(long value) {
		if (sampleRate == 1) {
			//没有采样率
			synchronized (this) {
				measurements.add(value);
			}
		} else if (sampleCounter.incrementAndGet() % sampleRate == 0) {
			//有采样率且命中采样
			synchronized (this) {
				measurements.add(value);
			}
		}
	}

	/**
	 * 计算单位时间内的metrics值, 存入该Metrics的Snapshot中，并清零原始数据.
	 */
	public HistogramMetric calculateMetric() {
		// 快照当前的数据，在计算时不阻塞新的metrics update.
		List<Long> snapshotList = null;
		synchronized (this) {
			snapshotList = measurements;
			measurements = new LinkedList<Long>();
		}

		if (snapshotList.isEmpty()) {
			return createEmptyMetric();
		}

		HistogramMetric metric = new HistogramMetric();
		int count = snapshotList.size();
		double sum = 0;

		if ((pcts != null) && (pcts.length > 0)) {
			// 按数值大小排序，以快速支持百分比
			Collections.sort(snapshotList);

			metric.min = snapshotList.get(0);
			metric.max = snapshotList.get(count - 1);

			for (long value : snapshotList) {
				sum += value;
			}

			for (Double pct : pcts) {
				metric.pcts.put(pct, getPercent(snapshotList, count, pct));
			}
		} else {
			// 不排序的算法，因为不需要支持百分位数
			metric.min = snapshotList.get(0);
			metric.max = metric.min;

			for (long value : snapshotList) {
				if (value < metric.min) {
					metric.min = value;
				}
				if (value > metric.max) {
					metric.max = value;
				}
				sum += value;
			}
		}

		metric.avg = sum / count;

		latestMetric = metric;
		return metric;
	}

	private HistogramMetric createEmptyMetric() {
		HistogramMetric metric = new HistogramMetric();
		for (Double pct : pcts) {
			metric.pcts.put(pct, 0L);
		}
		return metric;
	}

	/**
	 * 在已排序的List中，取出百分位数上的值
	 */
	private static Long getPercent(List<Long> snapshotList, int listCount, double pct) {
		double pos = (pct * (listCount + 1)) / 100;

		if (pos < 1) {
			return snapshotList.get(0);
		}

		if (pos >= listCount) {
			return snapshotList.get(listCount - 1);
		}

		return snapshotList.get((int) pos - 1);
	}

	public void reset() {
		synchronized (this) {
			this.measurements = new LinkedList<Long>();
		}
		this.sampleCounter.set(0);
		this.latestMetric = createEmptyMetric();
	}

	/**
	 * 配置需要计算的百分位，如99, 99.99
	 */
	public void setPcts(Double[] pcts) {
		this.pcts = pcts;
	}

	/**
	 * 采样率，1代表100%，2代表50%，10 代表10%
	 */
	public void setSampleRate(int sampleRate) {
		if (sampleRate <= 0) {
			sampleRate = 1;
		} else {
			this.sampleRate = sampleRate;
		}
	}

	@Override
	public String toString() {
		return "Histogram [latestMetric=" + latestMetric + ", measurements=" + measurements + ", pcts="
				+ Arrays.toString(pcts) + "]";
	}
}
