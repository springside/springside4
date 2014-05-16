/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics;

import java.util.LinkedHashMap;
import java.util.Map;

public class HistogramMetric {
	public long min;
	public long max;
	public double mean;
	public Map<Double, Long> pcts = new LinkedHashMap<Double, Long>();

	@Override
	public String toString() {
		return "HistogramMetric [min=" + min + ", max=" + max + ", mean=" + mean + ", pcts=" + pcts + "]";
	}
}
