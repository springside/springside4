/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

	private Double[] defaultPcts = new Double[] {};

	private ConcurrentMap<String, Gauge> gauges = new ConcurrentHashMap<String, Gauge>();
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
	 * 在注册中心注册Gauge.
	 */
	public void registerGauge(String name, Gauge gauge) {
		gauges.put(name, gauge);
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
	 * 在注册中心获取或创建Histogram, 并设置所需的百分比计算.
	 */
	public Histogram histogram(String name, Double... pcts) {
		if (histograms.containsKey(name)) {
			return histograms.get(name);
		} else {
			Histogram histogram = new Histogram(((pcts != null) && (pcts.length > 0)) ? pcts : defaultPcts);
			return register(histograms, name, histogram);
		}
	}

	/**
	 * 在注册中心获取或创建Timer, 并设置所需的百分比计算.
	 */
	public Timer timer(String name, Double... pcts) {
		if (timers.containsKey(name)) {
			return timers.get(name);
		} else {
			Timer timer = new Timer(((pcts != null) && (pcts.length > 0) ? pcts : defaultPcts));
			return register(timers, name, timer);
		}
	}

	/**
	 * 快速清理注册表中全部Metrics.
	 */
	public void clearAll() {
		for (MetricRegistryListener listener : listeners) {
			for (String key : gauges.keySet()) {
				listener.onGaugeRemoved(key);
			}

			for (String key : counters.keySet()) {
				listener.onCounterRemoved(key);
			}

			for (String key : histograms.keySet()) {
				listener.onHistogramRemoved(key);
			}

			for (String key : timers.keySet()) {
				listener.onTimerRemoved(key);
			}
		}

		gauges.clear();
		counters.clear();
		histograms.clear();
		timers.clear();
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
			if (newMetric instanceof Gauge) {
				listener.onGaugeAdded(name, (Gauge) newMetric);
			}
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
	 * 返回所有Gauge, 按名称排序.
	 */
	public Map<String, Gauge> getGauges() {
		return gauges;
	}

	/**
	 * 返回所有Counter, 按名称排序.
	 */
	public Map<String, Counter> getCounters() {
		return counters;
	}

	/**
	 * 返回所有Histogram, 按名称排序.
	 */

	public Map<String, Histogram> getHistograms() {
		return histograms;
	}

	/**
	 * 返回所有Timer, 按名称排序.
	 */
	public Map<String, Timer> getTimers() {
		return timers;
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
