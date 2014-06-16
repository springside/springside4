/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics.report;

import java.util.Map;

import org.springside.modules.metrics.CounterMetric;
import org.springside.modules.metrics.TimerMetric;
import org.springside.modules.metrics.HistogramMetric;

/**
 * Reporter的公共接口，被ReportScheduler定时调用。
 * 
 * @author Calvin
 * 
 */
public interface Reporter {
	void report(Map<String, CounterMetric> counters, Map<String, HistogramMetric> histograms,
			Map<String, TimerMetric> timers);
}
