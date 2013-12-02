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
		Counter counter = new Counter(defaultClock);
		return (Counter) register(name, counter);
	}

	public Histogram histogram(String name) {
		Histogram histogram = new Histogram(defaultPcts);
		return (Histogram) register(name, histogram);
	}

	public Execution execution(String name) {
		Execution execution = new Execution(defaultClock, defaultPcts);
		return (Execution) register(name, execution);
	}

	public Object register(String name, Object metric) {
		return metrics.putIfAbsent(name, metric);
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
