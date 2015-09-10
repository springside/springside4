/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics.reporter;

import java.util.Map;

import org.springside.modules.metrics.Counter;
import org.springside.modules.metrics.Gauge;
import org.springside.modules.metrics.Histogram;
import org.springside.modules.metrics.Timer;

/**
 * Reporter的公共接口，被ReportScheduler定时调用。
 * 
 * @author Calvin
 * 
 */
public interface Reporter {
	void report(Map<String, Gauge> gauges, Map<String, Counter> counters, Map<String, Histogram> histograms,
			Map<String, Timer> timers);
}
