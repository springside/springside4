package org.springside.examples.showcase.demos.metrics;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springside.modules.metrics.MetricRegistry;
import org.springside.modules.metrics.report.ConsoleReporter;
import org.springside.modules.metrics.report.GraphiteReporter;
import org.springside.modules.metrics.report.ReportScheduler;

public class MetricsManager {

	private ReportScheduler scheduler;

	@PostConstruct
	public void start() {
		GraphiteReporter graphiteReporter = new GraphiteReporter(new InetSocketAddress("localhost", 2003));
		ConsoleReporter consoleReporter = new ConsoleReporter();
		scheduler = new ReportScheduler(MetricRegistry.INSTANCE, consoleReporter, graphiteReporter);
		scheduler.start(10, TimeUnit.SECONDS);
	}

	@PreDestroy
	public void stop() {
		scheduler.stop();
	}
}
