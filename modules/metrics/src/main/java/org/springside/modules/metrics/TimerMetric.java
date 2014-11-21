/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics;

public class TimerMetric {
	public CounterMetric counterMetric;
	public HistogramMetric histogramMetric;

	@Override
	public String toString() {
		return "TimerMetric [counterMetric=" + counterMetric + ", histogramMetric=" + histogramMetric + "]";
	}
}
