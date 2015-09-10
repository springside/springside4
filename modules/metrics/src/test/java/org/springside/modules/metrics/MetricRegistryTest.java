/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.Test;

public class MetricRegistryTest {

	@Test
	public void counter() {
		MetricRegistry metricRegistry = new MetricRegistry();
		Counter counter = metricRegistry.counter(MetricRegistry.name("UserService", "getUser.counter"));
		assertThat(counter).isNotNull();

		Map<String, Counter> counters = metricRegistry.getCounters();

		Counter counter2 = counters.get("UserService.getUser.counter");
		assertThat(counter2).isNotNull().isSameAs(counter);

		Counter counter3 = metricRegistry.counter(MetricRegistry.name("UserService", "getUser.counter"));
		assertThat(counter3).isNotNull().isSameAs(counter);
	}

	@Test
	public void histogram() {
		MetricRegistry metricRegistry = new MetricRegistry();
		Histogram histogram = metricRegistry.histogram(MetricRegistry.name("UserService", "getUser.latency"));
		assertThat(histogram).isNotNull();

		Map<String, Histogram> histograms = metricRegistry.getHistograms();

		Histogram histogram2 = histograms.get("UserService.getUser.latency");
		assertThat(histogram2).isNotNull().isSameAs(histogram);

		Histogram histogram3 = metricRegistry.histogram(MetricRegistry.name("UserService", "getUser.latency"));
		assertThat(histogram3).isNotNull().isSameAs(histogram);

	}

	@Test
	public void execution() {
		MetricRegistry metricRegistry = new MetricRegistry();

		Timer execution = metricRegistry.timer(MetricRegistry.name("UserService", "getUser.execution"));
		assertThat(execution).isNotNull();

		Map<String, Timer> executions = metricRegistry.getTimers();

		Timer execution2 = executions.get("UserService.getUser.execution");
		assertThat(execution2).isNotNull().isSameAs(execution);

		Timer execution3 = metricRegistry.timer(MetricRegistry.name("UserService", "getUser.execution"));
		assertThat(execution3).isNotNull().isSameAs(execution);
	}

	@Test
	public void defaultPcts() {
		MetricRegistry metricRegistry = new MetricRegistry();

		// set pcts 60,70
		Histogram histogram = metricRegistry.histogram(MetricRegistry.name("UserService", "getUser.histogram.setPcts"),
				60d, 70d);

		for (int i = 1; i <= 100; i++) {
			histogram.update(i);
		}

		HistogramMetric metric = histogram.calculateMetric();

		assertThat(metric.pcts.get(60d)).isEqualTo(60);
		assertThat(metric.pcts.get(70d)).isEqualTo(70);

		// new default 50
		metricRegistry.setDefaultPcts(new Double[] { 50d });
		Histogram histogramWithNewDefaultPcts = metricRegistry.histogram(MetricRegistry.name("UserService",
				"getUser.histogram.newDefault"));

		for (int i = 1; i <= 100; i++) {
			histogramWithNewDefaultPcts.update(i);
		}

		metric = histogramWithNewDefaultPcts.calculateMetric();

		assertThat(metric.pcts.get(50d)).isEqualTo(50);
		assertThat(metric.pcts.get(90d)).isNull();
	}
}
