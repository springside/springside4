/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics.reporter;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.metrics.MetricRegistry;
import org.springside.modules.metrics.Reporter;
import org.springside.modules.metrics.metric.Counter;
import org.springside.modules.metrics.metric.CounterMetric;
import org.springside.modules.metrics.metric.Gauge;
import org.springside.modules.metrics.metric.Histogram;
import org.springside.modules.metrics.metric.HistogramMetric;
import org.springside.modules.metrics.metric.Timer;
import org.springside.modules.metrics.metric.TimerMetric;

/**
 * 使用Slf4j以INFO级别将Metrics打印到日志.
 * 
 * 默认logger name是"metrics"，可在构造函数中设定.用户需要在日志的配置文件中对该logger进行正确配置.
 * 
 * TODO: 另一个输出为JSON的实现
 */
public class Slf4jReporter implements Reporter {
	
	private static final String DEFAULT_LOGGER_NAME = "metrics";
	
	private Logger reportLogger;

	public Slf4jReporter() {
		this(DEFAULT_LOGGER_NAME);
	}

	public Slf4jReporter(String loggerName) {
		reportLogger = LoggerFactory.getLogger(loggerName);
		if (!reportLogger.isInfoEnabled()) {
			System.out.println("metrics logger " + loggerName + " is not config correctly");
		}
	}

	@Override
	public void report(Map<String, Gauge> gauges, Map<String, Counter> counters, Map<String, Histogram> histograms,
			Map<String, Timer> timers) {

		if (!reportLogger.isInfoEnabled()) {
			return;
		}

		for (Entry<String, Gauge> entry : MetricRegistry.getSortedMetrics(gauges).entrySet()) {
			logGauge(entry.getKey(), entry.getValue().latestMetric);
		}

		for (Entry<String, Counter> entry : MetricRegistry.getSortedMetrics(counters).entrySet()) {
			logCounter(entry.getKey(), entry.getValue().latestMetric);
		}

		for (Entry<String, Histogram> entry : MetricRegistry.getSortedMetrics(histograms).entrySet()) {
			logHistogram(entry.getKey(), entry.getValue().latestMetric);
		}

		for (Entry<String, Timer> entry : MetricRegistry.getSortedMetrics(timers).entrySet()) {
			logTimer(entry.getKey(), entry.getValue().latestMetric);
		}
	}

	private void logGauge(String name, Number gauge) {
		reportLogger.info("type=GAUGE, name={}, value={}", name, gauge);
	}

	private void logCounter(String name, CounterMetric counter) {
		reportLogger.info("type=COUNTER, name={}, totalCount={}, avgRate={}, latestRate={}", name, counter.totalCount,
				counter.avgRate, counter.latestRate);
	}

	private void logHistogram(String name, HistogramMetric histogram) {
		reportLogger.info("type=HISTOGRAM, name={}, min={}, max={}, avg={}{}", name, histogram.min, histogram.max,
				histogram.avg, buildPcts(histogram.pcts));
	}

	private void logTimer(String name, TimerMetric timer) {
		reportLogger.info(
				"type=TIMER, name={}, totalCount={}, avgRate={}, latestRate={}, minLatency={}ms, maxLatency={}ms, avgLatency={}ms{}",
				name, timer.counterMetric.totalCount, timer.counterMetric.avgRate, timer.counterMetric.latestRate,
				timer.histogramMetric.min, timer.histogramMetric.max, timer.histogramMetric.avg,
				buildPcts(timer.histogramMetric.pcts));
	}

	private static String buildPcts(Map<Double, Long> pcts) {
		StringBuilder builder = new StringBuilder();

		for (Entry<Double, Long> entry : pcts.entrySet()) {
			builder.append(", ").append(entry.getKey()).append("%<=").append(entry.getValue()).append("ms");
		}

		return builder.toString();
	}
}
