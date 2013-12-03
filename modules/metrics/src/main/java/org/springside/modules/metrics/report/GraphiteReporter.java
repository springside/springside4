package org.springside.modules.metrics.report;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.metrics.CounterMetric;
import org.springside.modules.metrics.ExecutionMetric;
import org.springside.modules.metrics.HistogramMetric;
import org.springside.modules.metrics.MetricRegistry;

public class GraphiteReporter implements Reporter {

	private static final Logger logger = LoggerFactory.getLogger(GraphiteReporter.class);

	private String prefix;

	private Graphite graphite;

	public GraphiteReporter(Graphite graphite) {
		this("metrics", graphite);
	}

	public GraphiteReporter(String prefix, Graphite graphite) {
		this.prefix = prefix;
		this.graphite = graphite;
	}

	@Override
	public void report(Map<String, CounterMetric> counters, Map<String, HistogramMetric> histograms,
			Map<String, ExecutionMetric> executions) {
		try {
			graphite.connect();
			long timestamp = System.currentTimeMillis() / 1000;

			for (Map.Entry<String, CounterMetric> entry : counters.entrySet()) {
				reportCounter(entry.getKey(), entry.getValue(), timestamp);
			}

			for (Map.Entry<String, HistogramMetric> entry : histograms.entrySet()) {
				reportHistogram(entry.getKey(), entry.getValue(), timestamp);
			}

			for (Map.Entry<String, ExecutionMetric> entry : executions.entrySet()) {
				reportExecution(entry.getKey(), entry.getValue(), timestamp);
			}

		} catch (IOException e) {
			logger.warn("Unable to report to Graphite", graphite, e);
		} finally {
			try {
				graphite.close();
			} catch (IOException e) {
				logger.warn("Error disconnecting from Graphite", graphite, e);
			}
		}

	}

	private void reportCounter(String name, CounterMetric counter, long timestamp) throws IOException {
		graphite.send(MetricRegistry.name(prefix, name, "count"), format(counter.count), timestamp);
		graphite.send(MetricRegistry.name(prefix, name, "lastRate"), format(counter.lastRate), timestamp);
		graphite.send(MetricRegistry.name(prefix, name, "meanRate"), format(counter.meanRate), timestamp);
	}

	private void reportHistogram(String name, HistogramMetric histogram, long timestamp) throws IOException {
		graphite.send(MetricRegistry.name(prefix, name, "min"), format(histogram.min), timestamp);
		graphite.send(MetricRegistry.name(prefix, name, "max"), format(histogram.max), timestamp);
		graphite.send(MetricRegistry.name(prefix, name, "mean"), format(histogram.mean), timestamp);
		for (Entry<Double, Long> pct : histogram.pcts.entrySet()) {
			graphite.send(MetricRegistry.name(prefix, name, format(pct.getKey()).replace('.', '_')),
					format(pct.getValue()), timestamp);
		}
	}

	private void reportExecution(String name, ExecutionMetric execution, long timestamp) throws IOException {
		graphite.send(MetricRegistry.name(prefix, name, "count"), format(execution.counter.count), timestamp);
		graphite.send(MetricRegistry.name(prefix, name, "lastRate"), format(execution.counter.lastRate), timestamp);
		graphite.send(MetricRegistry.name(prefix, name, "meanRate"), format(execution.counter.meanRate), timestamp);

		graphite.send(MetricRegistry.name(prefix, name, "min"), format(execution.histogram.min), timestamp);
		graphite.send(MetricRegistry.name(prefix, name, "max"), format(execution.histogram.max), timestamp);
		graphite.send(MetricRegistry.name(prefix, name, "mean"), format(execution.histogram.mean), timestamp);
		for (Entry<Double, Long> pct : execution.histogram.pcts.entrySet()) {
			graphite.send(MetricRegistry.name(prefix, name, format(pct.getKey()).replace('.', '_')),
					format(pct.getValue()), timestamp);
		}
	}

	private String format(Object o) {
		if (o instanceof Float) {
			return format(((Float) o).doubleValue());
		} else if (o instanceof Double) {
			return format(((Double) o).doubleValue());
		} else if (o instanceof Byte) {
			return format(((Byte) o).longValue());
		} else if (o instanceof Short) {
			return format(((Short) o).longValue());
		} else if (o instanceof Integer) {
			return format(((Integer) o).longValue());
		} else if (o instanceof Long) {
			return format(((Long) o).longValue());
		}
		return null;
	}

	private String format(long n) {
		return Long.toString(n);
	}

	private String format(double v) {
		return String.format(Locale.US, "%2.2f", v);
	}
}
