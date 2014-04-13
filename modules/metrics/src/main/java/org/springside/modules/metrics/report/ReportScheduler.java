/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics.report;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.metrics.Counter;
import org.springside.modules.metrics.CounterMetric;
import org.springside.modules.metrics.Execution;
import org.springside.modules.metrics.ExecutionMetric;
import org.springside.modules.metrics.Histogram;
import org.springside.modules.metrics.HistogramMetric;
import org.springside.modules.metrics.MetricRegistry;

public class ReportScheduler {
	private static final String SCHEDULER_NAME = "metrics-reporter";
	private static Logger logger = LoggerFactory.getLogger(ReportScheduler.class);
	private List<Reporter> reporters;
	private MetricRegistry metricRegistry;
	private ScheduledExecutorService executor;

	public ReportScheduler(MetricRegistry metricRegistry, Reporter... reporters) {
		this(metricRegistry, Arrays.asList(reporters));
	}

	public ReportScheduler(MetricRegistry metricRegistry, List<Reporter> reporters) {
		this.metricRegistry = metricRegistry;
		this.reporters = reporters;
		this.executor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(SCHEDULER_NAME));
	}

	public void addReporter(Reporter reporter) {
		reporters.add(reporter);
	}

	public void report() {
		SortedMap<String, Counter> counterMap = metricRegistry.getCounters();
		SortedMap<String, Histogram> histogramMap = metricRegistry.getHistograms();
		SortedMap<String, Execution> executionMap = metricRegistry.getExecutions();

		Map<String, CounterMetric> counterMetricMap = new LinkedHashMap<String, CounterMetric>();
		for (Entry<String, Counter> entry : counterMap.entrySet()) {
			counterMetricMap.put(entry.getKey(), entry.getValue().calculateMetric());
		}

		Map<String, HistogramMetric> histogramMetricMap = new LinkedHashMap<String, HistogramMetric>();
		for (Entry<String, Histogram> entry : histogramMap.entrySet()) {
			histogramMetricMap.put(entry.getKey(), entry.getValue().calculateMetric());
		}

		Map<String, ExecutionMetric> executionMetricMap = new LinkedHashMap<String, ExecutionMetric>();
		for (Entry<String, Execution> entry : executionMap.entrySet()) {
			executionMetricMap.put(entry.getKey(), entry.getValue().calculateMetric());
		}

		for (Reporter reporter : reporters) {
			reporter.report(counterMetricMap, histogramMetricMap, executionMetricMap);
		}
	}

	public void start(long period, TimeUnit unit) {
		executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					report();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}, period, period, unit);
		logger.info("metric reporters started.");
	}

	public void stop() {
		executor.shutdownNow();
		try {
			if (executor.awaitTermination(1, TimeUnit.SECONDS)) {
				logger.info("metric reporters stopped.");
			} else {
				logger.info("metric reporters can't stopped in 1 seconds, force stopped");
			}
		} catch (InterruptedException ignored) {
			// do nothing
		}
	}

	/**
	 * 给线程命名的线程工厂类，为了减少项目依赖，没有直接使用Guava里的实现。
	 */
	private static class NamedThreadFactory implements ThreadFactory {
		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String namePrefix;

		private NamedThreadFactory(String name) {
			final SecurityManager s = System.getSecurityManager();
			this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			this.namePrefix = name;
		}

		@Override
		public Thread newThread(Runnable r) {
			final Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
			t.setDaemon(true);
			if (t.getPriority() != Thread.NORM_PRIORITY) {
				t.setPriority(Thread.NORM_PRIORITY);
			}
			return t;
		}
	}
}
