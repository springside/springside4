/*******************************************************************************
 * Copyright (c) 2005, 2017 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.metrics.metric.Counter;
import org.springside.modules.metrics.metric.Gauge;
import org.springside.modules.metrics.metric.Histogram;
import org.springside.modules.metrics.metric.Timer;

/**
 * Reporter定时任务, 定期计算所有Metrics, 并调用所有Reporter进行汇报.
 * 
 * 由用户负责reporter的初始化及定时管理任务的启停.
 */
public class ReportScheduler {
	private static final String SCHEDULER_THREAD_NAME = "metrics-reporter";

	private static Logger logger = LoggerFactory.getLogger(ReportScheduler.class);

	private MetricRegistry metricRegistry;
	private List<Reporter> reporters;

	private ScheduledExecutorService executor;
	private volatile boolean started;

	public ReportScheduler(MetricRegistry metricRegistry, Reporter... reporters) {
		this(metricRegistry, new ArrayList<Reporter>());
		for (Reporter reporter : reporters) {
			this.addReporter(reporter);
		}
	}

	public ReportScheduler(MetricRegistry metricRegistry, List<Reporter> reporters) {
		this.metricRegistry = metricRegistry;
		this.reporters = reporters;
		this.executor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(SCHEDULER_THREAD_NAME));
	}

	public void addReporter(Reporter reporter) {
		reporters.add(reporter);
	}

	public void removeReporter(Reporter reporter) {
		reporters.remove(reporter);
	}

	/**
	 * 启动定时任务.
	 */
	public void start(long period, TimeUnit unit) {
		if (started) {
			throw new IllegalStateException("Scheduler had been started before");
		}
		executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					report();
				} catch (Throwable e) {
					logger.error(e.getMessage(), e);
				}
			}
		}, period, period, unit);
		started = true;
		logger.info("metric reporters started.");
	}

	/**
	 * 启动停止任务.
	 */
	public void stop() {
		if (!started) {
			throw new IllegalStateException("Scheduler hadn't been started before");
		}
		executor.shutdownNow();
		try {
			if (executor.awaitTermination(5, TimeUnit.SECONDS)) {
				logger.info("metric reporters stopped.");
			} else {
				logger.info("metric reporters can't stop in 5 seconds, force stopped.");
			}
			started = false;
		} catch (InterruptedException ignored) {
			// do nothing
		}
	}

	/**
	 * 定期计算所有Metrics, 并调用所有Reporter进行汇报.
	 */
	public void report() {

		// 取出所有Metrics, 未按名称排序.
		Map<String, Gauge> gaugeMap = metricRegistry.getGauges();
		Map<String, Counter> counterMap = metricRegistry.getCounters();
		Map<String, Histogram> histogramMap = metricRegistry.getHistograms();
		Map<String, Timer> timerMap = metricRegistry.getTimers();

		// 调度每个Metrics的caculateMetrics()方法
		// 计算单位时间内的metrics值, 存入该Metrics的Snapshot中，并清零原始数据
		for (Gauge gauge : gaugeMap.values()) {
			gauge.calculateMetric();
		}

		for (Counter counter : counterMap.values()) {
			counter.calculateMetric();
		}

		for (Histogram histogram : histogramMap.values()) {
			histogram.calculateMetric();
		}

		for (Timer timer : timerMap.values()) {
			timer.calculateMetric();
		}

		// 调度所有Reporters 输出 metrics值
		for (Reporter reporter : reporters) {
			reporter.report(gaugeMap, counterMap, histogramMap, timerMap);
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
			return t;
		}
	}
}
