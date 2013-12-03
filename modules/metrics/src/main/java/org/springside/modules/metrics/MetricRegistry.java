package org.springside.modules.metrics;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springside.modules.metrics.utils.Clock;

public class MetricRegistry {

	public static final MetricRegistry INSTANCE = new MetricRegistry();

	private Clock defaultClock = Clock.DEFAULT;

	private Double[] defaultPcts = new Double[] { 0.9 };

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
			Counter counter = new Counter(defaultClock);
			return (Counter) register(name, counter);
		}
	}

	public Histogram histogram(String name) {
		if (metrics.containsKey(name)) {
			return (Histogram) metrics.get(name);
		} else {
			Histogram histogram = new Histogram(defaultPcts);
			return (Histogram) register(name, histogram);
		}
	}

	public Execution execution(String name) {
		if (metrics.containsKey(name)) {
			return (Execution) metrics.get(name);
		} else {
			Execution execution = new Execution(defaultClock, defaultPcts);
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
		final TreeMap<String, T> result = new TreeMap<String, T>();
		for (Map.Entry<String, Object> entry : metrics.entrySet()) {
			if (klass.isInstance(entry.getValue())) {
				result.put(entry.getKey(), (T) entry.getValue());
			}
		}
		return Collections.unmodifiableSortedMap(result);
	}

	public void setDefaultClock(Clock clock) {
		this.defaultClock = clock;
	}

	public void setDefaultPcts(Double[] defaultPcts) {
		this.defaultPcts = defaultPcts;
	}
}
