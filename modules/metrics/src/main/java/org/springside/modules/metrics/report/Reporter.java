package org.springside.modules.metrics.report;

import java.util.SortedMap;

import org.springside.modules.metrics.report.metrics.CounterMetric;
import org.springside.modules.metrics.report.metrics.HistogramMetric;

public interface Reporter {
	void report(SortedMap<String, CounterMetric> counters, SortedMap<String, HistogramMetric> histograms);
}
