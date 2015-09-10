package org.springside.modules.metrics.exporter;

import org.springside.modules.metrics.Counter;
import org.springside.modules.metrics.Gauge;
import org.springside.modules.metrics.Histogram;
import org.springside.modules.metrics.Timer;

public interface MetricRegistryListener {

	void onGaugeAdded(String name, Gauge gauge);

	void onCounterAdded(String name, Counter counter);

	void onHistogramAdded(String name, Histogram histogram);

	void onTimerAdded(String name, Timer timer);

	void onGaugeRemoved(String name);

	void onCounterRemoved(String name);

	void onHistogramRemoved(String name);

	void onTimerRemoved(String name);
}
