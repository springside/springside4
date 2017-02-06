/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics.metric;

import java.util.Map;
import java.util.TreeMap;

/**
 * Histogram的统计值
 */
public class HistogramMetric {
	public long min;     //最小
	public long max;     //最大
	public double avg;  //平均
	public Map<Double, Long> pcts = new TreeMap<Double, Long>(); //百分位数

	@Override
	public String toString() {
		return "HistogramMetric [min=" + min + ", max=" + max + ", avg=" + avg + ", pcts=" + pcts + "]";
	}
}
