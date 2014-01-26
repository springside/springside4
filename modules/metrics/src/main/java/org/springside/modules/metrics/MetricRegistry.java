/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MetricRegistry {

	public static final MetricRegistry INSTANCE = new MetricRegistry();

	private Double[] defaultPcts = new Double[] { 90d };

	private ConcurrentMap<String, Object> metrics = new ConcurrentHashMap<String, Object>();

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

	public Counter counter(String name) {
		if (metrics.containsKey(name)) {
			return (Counter) metrics.get(name);
		} else {
			Counter counter = new Counter();
			return (Counter) register(name, counter);
		}
	}

	public Histogram histogram(String name) {
		return histogram(name, defaultPcts);
	}

	public Histogram histogram(String name, Double... pcts) {
		if (metrics.containsKey(name)) {
			return (Histogram) metrics.get(name);
		} else {
			Histogram histogram = new Histogram(pcts);
			return (Histogram) register(name, histogram);
		}
	}

	public Execution execution(String name) {
		return execution(name, defaultPcts);
	}

	public Execution execution(String name, Double... pcts) {
		if (metrics.containsKey(name)) {
			return (Execution) metrics.get(name);
		} else {
			Execution execution = new Execution(pcts);
			return (Execution) register(name, execution);
		}
	}

	public Object register(String name, Object newMetric) {
		Object existingMetric = metrics.putIfAbsent(name, newMetric);
		if (existingMetric != null) {
			return existingMetric;
		} else {
			return newMetric;
		}
	}

	public SortedMap<String, Counter> getCounters() {
		return getMetrics(Counter.class);
	}

	public SortedMap<String, Histogram> getHistograms() {
		return getMetrics(Histogram.class);
	}

	public SortedMap<String, Execution> getExecutions() {
		return getMetrics(Execution.class);
	}

	private <T> SortedMap<String, T> getMetrics(Class<T> klass) {
		final SortedMap<String, T> result = new TreeMap<String, T>();
		for (Map.Entry<String, Object> entry : metrics.entrySet()) {
			if (klass.isInstance(entry.getValue())) {
				result.put(entry.getKey(), (T) entry.getValue());
			}
		}
		return Collections.unmodifiableSortedMap(result);
	}

	public void setDefaultPcts(Double[] defaultPcts) {
		this.defaultPcts = defaultPcts;
	}
}
