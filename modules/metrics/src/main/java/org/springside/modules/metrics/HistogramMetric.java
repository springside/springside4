package org.springside.modules.metrics;

import java.util.LinkedHashMap;
import java.util.Map;

public class HistogramMetric {
	public long min;
	public long max;
	public double mean;
	public Map<Double, Long> pcts = new LinkedHashMap<Double, Long>();
}
