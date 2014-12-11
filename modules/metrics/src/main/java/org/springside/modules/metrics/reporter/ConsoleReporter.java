/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics.reporter;

import java.io.PrintStream;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springside.modules.metrics.Counter;
import org.springside.modules.metrics.CounterMetric;
import org.springside.modules.metrics.Gauge;
import org.springside.modules.metrics.Histogram;
import org.springside.modules.metrics.HistogramMetric;
import org.springside.modules.metrics.Timer;
import org.springside.modules.metrics.TimerMetric;

public class ConsoleReporter implements Reporter {

	private static final int CONSOLE_WIDTH = 80;
	private PrintStream output = System.out;

	@Override
	public void report(Map<String, Gauge> gauges, Map<String, Counter> counters, Map<String, Histogram> histograms,
			Map<String, Timer> timers) {

		printWithBanner(new Date().toString(), '=');
		output.println();

		if (!gauges.isEmpty()) {
			printWithBanner("-- Gaugues", '-');
			for (Map.Entry<String, Gauge> entry : getSortedMetrics(gauges).entrySet()) {
				output.println(entry.getKey());
				printGauge(entry.getValue());
			}
			output.println();
		}

		if (!counters.isEmpty()) {
			printWithBanner("-- Counters", '-');
			for (Map.Entry<String, Counter> entry : getSortedMetrics(counters).entrySet()) {
				output.println(entry.getKey());
				printCounter(entry.getValue().latestMetric);
			}
			output.println();
		}

		if (!histograms.isEmpty()) {
			printWithBanner("-- Histograms", '-');
			for (Map.Entry<String, Histogram> entry : getSortedMetrics(histograms).entrySet()) {
				output.println(entry.getKey());
				printHistogram(entry.getValue().latestMetric);
			}
			output.println();
		}

		if (!timers.isEmpty()) {
			printWithBanner("-- Timers", '-');
			for (Map.Entry<String, Timer> entry : getSortedMetrics(timers).entrySet()) {
				output.println(entry.getKey());
				printTimer(entry.getValue().latestMetric);
			}
			output.println();
		}
	}

	private void printWithBanner(String s, char c) {
		output.print(s);
		output.print(' ');
		for (int i = 0; i < (CONSOLE_WIDTH - s.length() - 1); i++) {
			output.print(c);
		}
		output.println();
	}

	private void printGauge(Gauge gauge) {
		output.printf("             value = %s%n", gauge.latestMetric);
	}

	private void printCounter(CounterMetric counter) {
		output.printf("      latest count = %d%n", counter.latestCount);
		output.printf("       total count = %d%n", counter.totalCount);
		output.printf("       latest rate = %d%n", counter.latestRate);
		output.printf("         mean rate = %d%n", counter.meanRate);
	}

	private void printHistogram(HistogramMetric histogram) {
		output.printf("               min = %d%n", histogram.min);
		output.printf("               max = %d%n", histogram.max);
		output.printf("              mean = %2.2f%n", histogram.mean);
		for (Entry<Double, Long> pct : histogram.pcts.entrySet()) {
			output.printf("           %2.2f%% <= %d %n", pct.getKey(), pct.getValue());
		}
	}

	private void printTimer(TimerMetric timer) {
		output.printf("      latest count = %d%n", timer.counterMetric.latestCount);
		output.printf("       total count = %d%n", timer.counterMetric.totalCount);
		output.printf("       latest rate = %d%n", timer.counterMetric.latestRate);
		output.printf("         mean rate = %d%n", timer.counterMetric.meanRate);
		output.printf("       min latency = %d ms%n", timer.histogramMetric.min);
		output.printf("       max latency = %d ms%n", timer.histogramMetric.max);
		output.printf("      mean latency = %2.2f ms%n", timer.histogramMetric.mean);
		for (Entry<Double, Long> pct : timer.histogramMetric.pcts.entrySet()) {
			output.printf("    %2.2f%% latency <= %d ms%n", pct.getKey(), pct.getValue());
		}
	}

	/**
	 * 返回按metrics name排序的Map.
	 * 
	 * 从get的性能考虑，没有使用ConcurrentSkipListMap而是仍然使用ConcurrentHashMap，因此每次报告时需要用TreeMap重新排序.
	 */
	private <T> SortedMap<String, T> getSortedMetrics(Map<String, T> metrics) {
		return new TreeMap<String, T>(metrics);
	}

}
