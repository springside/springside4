package org.springside.modules.metrics.report;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.net.SocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.metrics.CounterMetric;
import org.springside.modules.metrics.ExecutionMetric;
import org.springside.modules.metrics.HistogramMetric;
import org.springside.modules.metrics.MetricRegistry;

public class GraphiteReporter implements Reporter {

	private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");
	private static final Charset UTF_8 = Charset.forName("UTF-8");

	private static Logger logger = LoggerFactory.getLogger(GraphiteReporter.class);

	private String prefix;

	private InetSocketAddress address;
	private SocketFactory socketFactory;
	private Socket socket;
	private Writer writer;

	public GraphiteReporter(InetSocketAddress address) {
		this(address, "metrics");
	}

	public GraphiteReporter(InetSocketAddress address, String prefix) {
		this.prefix = prefix;
		this.address = address;
		this.socketFactory = SocketFactory.getDefault();
	}

	@Override
	public void report(Map<String, CounterMetric> counters, Map<String, HistogramMetric> histograms,
			Map<String, ExecutionMetric> executions) {
		try {
			connect();
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

			flush();

		} catch (IOException e) {
			logger.warn("Unable to report to Graphite", e);
		} finally {
			try {
				close();
			} catch (IOException e) {
				logger.warn("Error disconnecting from Graphite", e);
			}
		}
	}

	private void reportCounter(String name, CounterMetric counter, long timestamp) throws IOException {
		send(MetricRegistry.name(prefix, name, "count"), format(counter.count), timestamp);
	}

	private void reportHistogram(String name, HistogramMetric histogram, long timestamp) throws IOException {
		send(MetricRegistry.name(prefix, name, "min"), format(histogram.min), timestamp);
		send(MetricRegistry.name(prefix, name, "max"), format(histogram.max), timestamp);
		send(MetricRegistry.name(prefix, name, "mean"), format(histogram.mean), timestamp);
		for (Entry<Double, Long> pct : histogram.pcts.entrySet()) {
			send(MetricRegistry.name(prefix, name, format(pct.getKey()).replace('.', '_')), format(pct.getValue()),
					timestamp);
		}
	}

	private void reportExecution(String name, ExecutionMetric execution, long timestamp) throws IOException {
		send(MetricRegistry.name(prefix, name, "count"), format(execution.counter.count), timestamp);

		send(MetricRegistry.name(prefix, name, "min"), format(execution.histogram.min), timestamp);
		send(MetricRegistry.name(prefix, name, "max"), format(execution.histogram.max), timestamp);
		send(MetricRegistry.name(prefix, name, "mean"), format(execution.histogram.mean), timestamp);
		for (Entry<Double, Long> pct : execution.histogram.pcts.entrySet()) {
			send(MetricRegistry.name(prefix, name, format(pct.getKey()).replace('.', '_')), format(pct.getValue()),
					timestamp);
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

	private void connect() throws IllegalStateException, IOException {
		if (socket != null) {
			throw new IllegalStateException("Already connected");
		}

		this.socket = socketFactory.createSocket(address.getAddress(), address.getPort());
		this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8));
	}

	/**
	 * Sends the given measurement to the server.
	 * 
	 * @param name the name of the metric
	 * @param value the value of the metric
	 * @param timestamp the timestamp of the metric
	 * @throws IOException if there was an error sending the metric
	 */
	private void send(String name, String value, long timestamp) throws IOException {
		try {
			writer.write(sanitize(name));
			writer.write(' ');
			writer.write(sanitize(value));
			writer.write(' ');
			writer.write(Long.toString(timestamp));
			writer.write('\n');
		} catch (IOException e) {
			throw e;
		}
	}

	private String sanitize(String s) {
		return WHITESPACE.matcher(s).replaceAll("-");
	}

	private void flush() throws IOException {
		writer.flush();
	}

	private void close() throws IOException {
		if (socket != null) {
			socket.close();
		}
		this.socket = null;
		this.writer = null;
	}
}
