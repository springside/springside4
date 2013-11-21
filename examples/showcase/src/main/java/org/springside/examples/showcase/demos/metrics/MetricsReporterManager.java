package org.springside.examples.showcase.demos.metrics;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;

public class MetricsReporterManager {

	private static Logger logger = LoggerFactory.getLogger(MetricsReporterManager.class);

	@Autowired
	private MetricRegistry metricRegistry;

	private GraphiteReporter graphiteReporter;
	private JmxReporter jmxReporter;

	@PostConstruct
	public void start() {
		Graphite graphite = new Graphite(new InetSocketAddress("localhost", 2003));
		graphiteReporter = GraphiteReporter.forRegistry(metricRegistry).prefixedWith("metrics").build(graphite);
		graphiteReporter.start(30, TimeUnit.SECONDS);

		jmxReporter = JmxReporter.forRegistry(metricRegistry).build();
		jmxReporter.start();

		logger.info("Started Metrics Reporter");
	}

	@PreDestroy
	public void stop() {
		graphiteReporter.stop();
		jmxReporter.stop();

		logger.info("Stopped Metrics Reporter");
	}
}
