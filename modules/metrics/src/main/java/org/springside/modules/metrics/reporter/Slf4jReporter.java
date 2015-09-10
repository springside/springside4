/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics.reporter;

import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.metrics.Counter;
import org.springside.modules.metrics.CounterMetric;
import org.springside.modules.metrics.Gauge;
import org.springside.modules.metrics.Histogram;
import org.springside.modules.metrics.HistogramMetric;
import org.springside.modules.metrics.Timer;
import org.springside.modules.metrics.TimerMetric;

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
	public void report(Map<String, Gauge> gauges, Map<String, Counter> counters, Map<String, Histogram> histograms,
			Map<String, Timer> timers) {

		for (Entry<String, Gauge> entry : getSortedMetrics(gauges).entrySet()) {
			logGauge(entry.getKey(), entry.getValue().latestMetric);
		}

		for (Entry<String, Counter> entry : getSortedMetrics(counters).entrySet()) {
			logCounter(entry.getKey(), entry.getValue().latestMetric);
		}

		for (Entry<String, Histogram> entry : getSortedMetrics(histograms).entrySet()) {
			logHistogram(entry.getKey(), entry.getValue().latestMetric);
		}

		for (Entry<String, Timer> entry : getSortedMetrics(timers).entrySet()) {
			logTimer(entry.getKey(), entry.getValue().latestMetric);
		}
	}

	private void logGauge(String name, Number gauge) {
		reportLogger.info("type=GAUGE, name={}, value={}", name, gauge);
	}

	private void logCounter(String name, CounterMetric counter) {
		reportLogger.info("type=COUNTER, name={}, totalCount={}, meanRate={}, latestRate={}", name, counter.totalCount,
				counter.meanRate, counter.latestRate);
	}

	private void logHistogram(String name, HistogramMetric histogram) {
		reportLogger.info("type=HISTOGRAM, name={}, min={}, max={}, mean={}{}", name, histogram.min, histogram.max,
				histogram.mean, buildPcts(histogram.pcts));
	}

	private void logTimer(String name, TimerMetric timer) {
		reportLogger
				.info("type=TIMER, name={}, totalCount={}, meanRate={}, latestRate={}, minLatency={}ms, maxLatency={}ms, meanLatency={}ms{}",
						name, timer.counterMetric.totalCount, timer.counterMetric.meanRate,
						timer.counterMetric.latestRate, timer.histogramMetric.min, timer.histogramMetric.max,
						timer.histogramMetric.mean, buildPcts(timer.histogramMetric.pcts));
	}

	private String buildPcts(Map<Double, Long> pcts) {
		StringBuilder builder = new StringBuilder();

		for (Entry<Double, Long> entry : pcts.entrySet()) {
			builder.append(", ").append(entry.getKey()).append("%<=").append(entry.getValue()).append("ms");
		}

		return builder.toString();
	}

	/**
	 * 返回按metrics name排序的Map.
	 * 
	 * 从get的性能考虑，没有使用ConcurrentSkipListMap而是仍然使用ConcurrentHashMap，因此每次顺序报告时需要用TreeMap重新排序.
	 */
	private <T> SortedMap<String, T> getSortedMetrics(Map<String, T> metrics) {
		return new TreeMap<String, T>(metrics);
	}
}
