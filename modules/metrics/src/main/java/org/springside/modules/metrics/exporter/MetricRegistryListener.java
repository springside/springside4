package org.springside.modules.metrics.exporter;

import org.springside.modules.metrics.Counter;
import org.springside.modules.metrics.Histogram;
import org.springside.modules.metrics.Timer;

public interface MetricRegistryListener {

	void onCounterAdded(String name, Counter counter);

	void onHistogramAdded(String name, Histogram histogram);

	void onTimerAdded(String name, Timer timer);
}
