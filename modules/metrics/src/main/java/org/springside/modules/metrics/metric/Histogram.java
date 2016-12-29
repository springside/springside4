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

/**
 * Histogram数据类型, 主要用于计算Latency.
 * 
 * 报告Report间隔时间内数值的最小/最大，平均值，以及某个百分比的请求数都小于的值.
 */
public class Histogram {

	public HistogramMetric latestMetric;// Snapshot值

	private Double[] pcts; //配置需要计算的百分位
	
	private LinkedList<Long> measurements; //统计周期内的原始数据
	
	public Histogram(Double... pcts) {
		this.pcts = pcts;
		measurements = new LinkedList<Long>();
		latestMetric = createEmptyMetric();
	}

	public void update(long value) {
		synchronized (measurements) {
			measurements.add(value);
		}
	}

	/**
	 * 计算单位时间内的metrics值, 存入该Metrics的Snapshot中，并清零原始数据.
	 */
	public HistogramMetric calculateMetric() {
		// 快照当前的数据，在计算时不阻塞新的metrics update.
		List<Long> snapshotList = measurements;
		measurements = new LinkedList<Long>();

		if (snapshotList.isEmpty()) {
			return createEmptyMetric();
		}

		HistogramMetric metric = new HistogramMetric();
		int count = snapshotList.size();
		double sum = 0;

		if ((pcts != null) && (pcts.length > 0)) {
			// 按数值大小排序，以快速支持百分比过滤
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
			// 不排序的算法，因为不需要支持百分比过滤
			metric.min = snapshotList.get(0);
			metric.max = snapshotList.get(0);

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

		metric.mean = sum / count;
		
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

	private Long getPercent(List<Long> snapshotList, int count, double pct) {

		final double pos = (pct * (count + 1)) / 100;

		if (pos < 1) {
			return snapshotList.get(0);
		}

		if (pos >= count) {
			return snapshotList.get(count - 1);
		}

		return snapshotList.get((int) pos - 1);
	}

	@Override
	public String toString() {
		return "Histogram [latestMetric=" + latestMetric + ", measurements=" + measurements + ", pcts="
				+ Arrays.toString(pcts) + "]";
	}
}
