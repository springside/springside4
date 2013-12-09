package org.springside.modules.metrics.report;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.metrics.CounterMetric;
import org.springside.modules.metrics.ExecutionMetric;
import org.springside.modules.metrics.HistogramMetric;

public class Slf4jReporter implements Reporter {
	private Logger reportLogger;

	public Slf4jReporter() {
		this("metrics");
	}

	public Slf4jReporter(String loggerName) {
		reportLogger = LoggerFactory.getLogger(loggerName);
	}

	@Override
	public void report(Map<String, CounterMetric> counters, Map<String, HistogramMetric> histograms,
			Map<String, ExecutionMetric> executions) {
		for (Entry<String, CounterMetric> entry : counters.entrySet()) {
			logCounter(entry.getKey(), entry.getValue());
		}

		for (Entry<String, HistogramMetric> entry : histograms.entrySet()) {
			logHistogram(entry.getKey(), entry.getValue());
		}

		for (Entry<String, ExecutionMetric> entry : executions.entrySet()) {
			logExecution(entry.getKey(), entry.getValue());
		}

	}

	private void logCounter(String name, CounterMetric counter) {
		reportLogger.info("type=COUNTER, name={}, count={}, lastRate={}, meanRate={}", name, counter.count,
				counter.lastRate, counter.meanRate);
	}

	private void logHistogram(String name, HistogramMetric histogram) {
		reportLogger.info("type=HISTOGRAM, name={}, min={}, max={}, mean={}{}", name, histogram.min, histogram.max,
				histogram.mean, buildPcts(histogram.pcts));
	}

	private void logExecution(String name, ExecutionMetric execution) {
		reportLogger.info("type=EXECUTION, name={}, count={}, lastRate={}, meanRate={}, min={}, max={}, mean={}{}",
				name, execution.counter.count, execution.counter.lastRate, execution.counter.meanRate,
				execution.histogram.min, execution.histogram.max, execution.histogram.mean,
				buildPcts(execution.histogram.pcts));
	}

	private String buildPcts(Map<Double, Long> pcts) {
		StringBuilder builder = new StringBuilder();

		for (Entry<Double, Long> entry : pcts.entrySet()) {
			builder.append(", p").append(entry.getKey()).append("=").append(entry.getValue());
		}

		return builder.toString();
	}
}
