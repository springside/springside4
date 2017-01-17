/*******************************************************************************
 * Copyright (c) 2005, 2017 springside.github.io
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

import org.springside.modules.metrics.metric.Counter;
import org.springside.modules.metrics.metric.Gauge;
import org.springside.modules.metrics.metric.Histogram;
import org.springside.modules.metrics.metric.Timer;

/**
 * 注册中心, 用户创建Metrics的入口. 
 * 
 * 支持多线程并发的取得或创建metrics.
 */
public class MetricRegistry {

	public static final MetricRegistry INSTANCE = new MetricRegistry();

	private Double[] defaultPcts = new Double[] {};

	// 从get的性能考虑，没有使用ConcurrentSkipListMap而是仍然使用ConcurrentHashMap.
	private ConcurrentMap<String, Gauge> gauges = new ConcurrentHashMap<String, Gauge>();
	private ConcurrentMap<String, Counter> counters = new ConcurrentHashMap<String, Counter>();
	private ConcurrentMap<String, Histogram> histograms = new ConcurrentHashMap<String, Histogram>();
	private ConcurrentMap<String, Timer> timers = new ConcurrentHashMap<String, Timer>();

	private List<MetricRegistryListener> listeners = new ArrayList<MetricRegistryListener>();

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
	 * 
	 * @param pcts 设定百分位数，可选值如99, 99.99，为空时使用MetricRegistry的默认值
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
	 * 
	 * @param pcts 设定百分位数，可选值如99, 99.99，为空时使用MetricRegistry的默认值
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
		//通知Listener
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

		//清空注册
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

	/**
	 * 通知Listener有新Metrics加入
	 */
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
	 * 返回所有Gauge, 无序.
	 */
	public Map<String, Gauge> getGauges() {
		return gauges;
	}

	/**
	 * 返回所有Counter, 无序.
	 */
	public Map<String, Counter> getCounters() {
		return counters;
	}

	/**
	 * 返回所有Histogram, 无序.
	 */
	public Map<String, Histogram> getHistograms() {
		return histograms;
	}

	/**
	 * 返回所有Timer, 无序.
	 */
	public Map<String, Timer> getTimers() {
		return timers;
	}

	/**
	 * 重新设置默认的百分位数.
	 */
	public void setDefaultPcts(Double[] defaultPcts) {
		this.defaultPcts = defaultPcts;
	}

	/**
	 * 加入Listener，侦听Metrics的增删变化
	 */
	public void addListener(MetricRegistryListener listener) {
		listeners.add(listener);
	}

	/**
	 * 格式化以"."分割的Metrics Name的辅助函数.
	 */
	public static String name(String name, String... subNames) {
		StringBuilder builder = new StringBuilder(name);
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
	 * 返回按metrics name排序的Map的辅助函数.
	 * 
	 * 从get的性能考虑，没有使用ConcurrentSkipListMap，而某些Reporter可能需要固定的顺序
	 */
	public static <T> SortedMap<String, T> getSortedMetrics(Map<String, T> metrics) {
		return new TreeMap<String, T>(metrics);
	}
}
