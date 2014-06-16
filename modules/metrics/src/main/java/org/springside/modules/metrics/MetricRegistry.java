/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springside.modules.metrics.exporter.MetricRegistryListener;

/**
 * 注册中心, 用户创建Metrics的入口.
 * 支持多线程并发的取得或创建metrics.
 * 在报告时，返回按name排序的metrics map.
 */
public class MetricRegistry {

	public static final MetricRegistry INSTANCE = new MetricRegistry();

	private Double[] defaultPcts = new Double[] { 90d };

	private ConcurrentMap<String, Counter> counters = new ConcurrentHashMap<String, Counter>();
	private ConcurrentMap<String, Histogram> histograms = new ConcurrentHashMap<String, Histogram>();
	private ConcurrentMap<String, Timer> timers = new ConcurrentHashMap<String, Timer>();

	private List<MetricRegistryListener> listeners = new ArrayList<MetricRegistryListener>();

	/**
	 * 格式化以"."分割的Metrics Name的辅助函数.
	 */
	public static String name(String name, String... subNames) {
		final StringBuilder builder = new StringBuilder(name);
		if (subNames != null) {
			for (String s : subNames) {
				if ((s != null) && !s.isEmpty()) {
					builder.append('.').append(s);
				}
			}
		}
		return builder.toString();
	}

	/**
	 * 在注册中心获取或创建Counter.
	 */
	public Counter counter(String name) {
		if (counters.containsKey(name)) {
			return counters.get(name);
		} else {
			Counter counter = new Counter();
			return register(counters, name, counter);
		}
	}

	/**
	 * 在注册中心获取或创建Histogram, 使用默认的百分比计算设置(90%).
	 */
	public Histogram histogram(String name) {
		return histogram(name, defaultPcts);
	}

	/**
	 * 在注册中心获取或创建Histogram, 并设置所需的百分比计算.
	 */
	public Histogram histogram(String name, Double... pcts) {
		if (histograms.containsKey(name)) {
			return histograms.get(name);
		} else {
			Histogram histogram = new Histogram(pcts);
			return register(histograms, name, histogram);
		}
	}

	/**
	 * 在注册中心获取或创建Timer, 使用默认的百分比计算设置(90%).
	 */
	public Timer timer(String name) {
		return timer(name, defaultPcts);
	}

	/**
	 * 在注册中心获取或创建Timer, 并设置所需的百分比计算.
	 */
	public Timer timer(String name, Double... pcts) {
		if (timers.containsKey(name)) {
			return timers.get(name);
		} else {
			Timer timer = new Timer(pcts);
			return register(timers, name, timer);
		}
	}

	private <T> T register(ConcurrentMap<String, T> metrics, String name, T newMetric) {
		T existingMetric = metrics.putIfAbsent(name, newMetric);
		if (existingMetric != null) {
			return existingMetric;
		} else {
			notifyNewMetric(name, newMetric);
			return newMetric;
		}
	}

	private void notifyNewMetric(String name, Object newMetric) {
		for (MetricRegistryListener listener : listeners) {
			if (newMetric instanceof Counter) {
				listener.onCounterAdded(name, (Counter) newMetric);
			}
			if (newMetric instanceof Histogram) {
				listener.onHistogramAdded(name, (Histogram) newMetric);
			}
			if (newMetric instanceof Timer) {
				listener.onTimerAdded(name, (Timer) newMetric);
			}

		}
	}

	/**
	 * 返回所有Counter, 按名称排序.
	 */
	public SortedMap<String, Counter> getCounters() {
		return getMetrics(counters);
	}

	/**
	 * 返回所有Histogram, 按名称排序.
	 */

	public SortedMap<String, Histogram> getHistograms() {
		return getMetrics(histograms);
	}

	/**
	 * 返回所有Timer, 按名称排序.
	 */
	public SortedMap<String, Timer> getTimers() {
		return getMetrics(timers);
	}

	/**
	 * 返回按metrics name排序的Map.
	 * 
	 * 从get的性能考虑，没有使用ConcurrentSkipListMap而是仍然使用ConcurrentHashMap，因此每次报告时需要用TreeMap重新排序.
	 */
	private <T> SortedMap<String, T> getMetrics(Map<String, T> metrics) {
		return new TreeMap<String, T>(metrics);
	}

	/**
	 * 重新设置默认的百分比设置.
	 */
	public void setDefaultPcts(Double[] defaultPcts) {
		this.defaultPcts = defaultPcts;
	}

	/**
	 * Exporter将自己加为Listener.
	 */
	public void addListener(MetricRegistryListener listener) {
		listeners.add(listener);
	}
}
