/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
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

	private boolean graphiteEnabled = false;

	@PostConstruct
	public void start() {
		ConsoleReporter consoleReporter = new ConsoleReporter();
		scheduler = new ReportScheduler(MetricRegistry.INSTANCE, consoleReporter);

		if (graphiteEnabled) {
			GraphiteReporter graphiteReporter = new GraphiteReporter(new InetSocketAddress("localhost", 2003));
			scheduler.addReporter(graphiteReporter);
		}

		scheduler.start(10, TimeUnit.SECONDS);
	}

	@PreDestroy
	public void stop() {
		scheduler.stop();
	}

	public void setGraphiteEnabled(boolean graphiteEnabled) {
		this.graphiteEnabled = graphiteEnabled;
	}
}
