/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics.report;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.metrics.CounterMetric;
import org.springside.modules.metrics.ExecutionMetric;
import org.springside.modules.metrics.HistogramMetric;

/**
 * 使用Slf4j将Metrics打印到日志，默认logger name是"metrics"，可在构造函数中设定。
 * 
 * @author calvin
 */
public class Slf4jReporter implements Reporter {
	private Logger reportLogger;

	public Slf4jReporter() {
		this("metrics");
	}

	public Slf4jReporter(String loggerName) {
		reportLogger = LoggerFactory.getLogger(loggerName);
	}

	@Override
	public void report(Map<String, CounterMetric> counters, Map<String, HistogramMetric> histograms,
			Map<String, ExecutionMetric> executions) {
		for (Entry<String, CounterMetric> entry : counters.entrySet()) {
			logCounter(entry.getKey(), entry.getValue());
		}

		for (Entry<String, HistogramMetric> entry : histograms.entrySet()) {
			logHistogram(entry.getKey(), entry.getValue());
		}

		for (Entry<String, ExecutionMetric> entry : executions.entrySet()) {
			logExecution(entry.getKey(), entry.getValue());
		}

	}

	private void logCounter(String name, CounterMetric counter) {
		reportLogger.info("type=COUNTER, name={}, count={}, lastRate={}, meanRate={}", name, counter.totalCount,
				counter.lastRate, counter.meanRate);
	}

	private void logHistogram(String name, HistogramMetric histogram) {
		reportLogger.info("type=HISTOGRAM, name={}, min={}, max={}, mean={}{}", name, histogram.min, histogram.max,
				histogram.mean, buildPcts(histogram.pcts));
	}

	private void logExecution(String name, ExecutionMetric execution) {
		reportLogger.info("type=EXECUTION, name={}, count={}, lastRate={}, meanRate={}, min={}ms, max={}ms, mean={}ms",
				name, execution.counterMetric.totalCount, execution.counterMetric.lastRate,
				execution.counterMetric.meanRate, execution.histogramMetric.min, execution.histogramMetric.max,
				execution.histogramMetric.mean, buildPcts(execution.histogramMetric.pcts));
	}

	private String buildPcts(Map<Double, Long> pcts) {
		StringBuilder builder = new StringBuilder();

		for (Entry<Double, Long> entry : pcts.entrySet()) {
			builder.append(", ").append(entry.getKey()).append("%<=").append(entry.getValue()).append("ms");
		}

		return builder.toString();
	}
}
