/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics.reporter;

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
import org.springside.modules.metrics.Counter;
import org.springside.modules.metrics.CounterMetric;
import org.springside.modules.metrics.Gauge;
import org.springside.modules.metrics.Histogram;
import org.springside.modules.metrics.HistogramMetric;
import org.springside.modules.metrics.MetricRegistry;
import org.springside.modules.metrics.Timer;
import org.springside.modules.metrics.TimerMetric;

public class GraphiteReporter implements Reporter {

	public static final String DEFAULT_PREFIX = "metrics";

	private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");
	private static final Charset UTF_8 = Charset.forName("UTF-8");

	private static Logger logger = LoggerFactory.getLogger(GraphiteReporter.class);

	private String prefix;

	private InetSocketAddress address;
	private SocketFactory socketFactory;
	private Socket socket;
	private Writer writer;

	// use to only print connection error message once.
	private GraphiteConnStatus graphiteConnStatus = GraphiteConnStatus.CONN_OK;

	public GraphiteReporter(InetSocketAddress address) {
		this(address, DEFAULT_PREFIX);
	}

	public GraphiteReporter(InetSocketAddress address, String prefix) {
		this.prefix = prefix;
		this.address = address;
		this.socketFactory = SocketFactory.getDefault();
	}

	@Override
	public void report(Map<String, Gauge> gauges, Map<String, Counter> counters, Map<String, Histogram> histograms,
			Map<String, Timer> timers) {
		try {
			connect();
			long timestamp = System.currentTimeMillis() / 1000;

			for (Map.Entry<String, Gauge> entry : gauges.entrySet()) {
				reportGauge(entry.getKey(), entry.getValue().latestMetric, timestamp);
			}

			for (Map.Entry<String, Counter> entry : counters.entrySet()) {
				reportCounter(entry.getKey(), entry.getValue().latestMetric, timestamp);
			}

			for (Map.Entry<String, Histogram> entry : histograms.entrySet()) {
				reportHistogram(entry.getKey(), entry.getValue().latestMetric, timestamp);
			}

			for (Map.Entry<String, Timer> entry : timers.entrySet()) {
				reportTimer(entry.getKey(), entry.getValue().latestMetric, timestamp);
			}

			flush();

			onConnSuccess();
		} catch (IOException e) {
			onConnFail(e);
		} finally {
			try {
				close();
			} catch (IOException e) {
				logger.warn("Error disconnecting from Graphite", e);
			}
		}
	}

	private void reportGauge(String name, Number gauge, long timestamp) throws IOException {
		send(MetricRegistry.name(prefix, name, "gauge"), format(gauge), timestamp);
	}

	private void reportCounter(String name, CounterMetric counter, long timestamp) throws IOException {
		send(MetricRegistry.name(prefix, name, "count"), format(counter.latestCount), timestamp);
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

	private void reportTimer(String name, TimerMetric timer, long timestamp) throws IOException {
		send(MetricRegistry.name(prefix, name, "count"), format(timer.counterMetric.latestCount), timestamp);

		send(MetricRegistry.name(prefix, name, "min"), format(timer.histogramMetric.min), timestamp);
		send(MetricRegistry.name(prefix, name, "max"), format(timer.histogramMetric.max), timestamp);
		send(MetricRegistry.name(prefix, name, "mean"), format(timer.histogramMetric.mean), timestamp);
		for (Entry<Double, Long> pct : timer.histogramMetric.pcts.entrySet()) {
			send(MetricRegistry.name(prefix, name, format(pct.getKey()).replace('.', '_')), format(pct.getValue()),
					timestamp);
		}
	}

	private void connect() throws IllegalStateException, IOException {
		if (socket != null) {
			throw new IllegalStateException("Already connected");
		}

		this.socket = socketFactory.createSocket(address.getAddress(), address.getPort());
		this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8));
	}

	private void send(String name, String value, long timestamp) throws IOException {
		try {
			writer.write(sanitize(name));
			writer.write(' ');
			writer.write(sanitize(value));
			writer.write(' ');
			writer.write(format(timestamp));
			writer.write('\n');
		} catch (IOException e) {
			throw e;
		}
	}

	private void flush() throws IOException {
		writer.flush();
	}

	private void close() throws IOException {
		if (writer != null) {
			writer.flush();
		}

		if (socket != null) {
			socket.close();
		}
		this.socket = null;
		this.writer = null;
	}

	private String format(long n) {
		return Long.toString(n);
	}

	private String format(double v) {
		return String.format(Locale.US, "%2.2f", v);
	}

	private String format(Number o) {
		if (o instanceof Float) {
			return format(((Float) o).doubleValue());
		} else if (o instanceof Double) {
			return format(((Double) o).doubleValue());
		} else if (o instanceof Short) {
			return format(((Short) o).longValue());
		} else if (o instanceof Integer) {
			return format(((Integer) o).longValue());
		} else if (o instanceof Long) {
			return format(((Long) o).longValue());
		}
		return null;
	}

	private String sanitize(String s) {
		return WHITESPACE.matcher(s).replaceAll("-");
	}

	private void onConnFail(Exception exception) {
		if (graphiteConnStatus != GraphiteConnStatus.CONN_NOK) {
			logger.warn("Unable to report to Graphite", exception);
			graphiteConnStatus = GraphiteConnStatus.CONN_NOK;
		}
	}

	private void onConnSuccess() {
		if (graphiteConnStatus != GraphiteConnStatus.CONN_OK) {
			logger.info("Graphite connection is recovered.");
			graphiteConnStatus = GraphiteConnStatus.CONN_OK;
		}
	}

	private enum GraphiteConnStatus {
		CONN_OK, CONN_NOK
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
