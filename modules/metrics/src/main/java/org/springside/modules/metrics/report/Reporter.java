package org.springside.modules.metrics.report;

import java.util.Map;

import org.springside.modules.metrics.CounterMetric;
import org.springside.modules.metrics.ExecutionMetric;
import org.springside.modules.metrics.HistogramMetric;

public interface Reporter {
	void report(Map<String, CounterMetric> counters, Map<String, HistogramMetric> histograms,
			Map<String, ExecutionMetric> executions);
}
