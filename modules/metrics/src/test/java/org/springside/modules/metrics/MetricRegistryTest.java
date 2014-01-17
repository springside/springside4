/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class MetricRegistryTest {

	@Test
	public void counter() {
		MetricRegistry metricRegistry = new MetricRegistry();
		Counter counter = metricRegistry.counter(MetricRegistry.name("UserService", "getUser.counter"));
		assertNotNull(counter);

		Map<String, Counter> counters = metricRegistry.getCounters();

		Counter counter2 = counters.get("UserService.getUser.counter");
		assertNotNull(counter2);
		assertTrue(counter == counter2);

		Counter counter3 = metricRegistry.counter(MetricRegistry.name("UserService", "getUser.counter"));
		assertNotNull(counter3);
		assertTrue(counter == counter3);
	}

	@Test
	public void histogram() {
		MetricRegistry metricRegistry = new MetricRegistry();
		Histogram histogram = metricRegistry.histogram(MetricRegistry.name("UserService", "getUser.latency"));
		assertNotNull(histogram);

		Map<String, Histogram> histograms = metricRegistry.getHistograms();

		Histogram histogram2 = histograms.get("UserService.getUser.latency");
		assertNotNull(histogram2);
		assertTrue(histogram == histogram2);

		Histogram histogram3 = metricRegistry.histogram(MetricRegistry.name("UserService", "getUser.latency"));
		assertNotNull(histogram3);
		assertTrue(histogram == histogram3);
	}

	@Test
	public void execution() {
		MetricRegistry metricRegistry = new MetricRegistry();

		Execution execution = metricRegistry.execution(MetricRegistry.name("UserService", "getUser.execution"));
		assertNotNull(execution);

		Map<String, Execution> executions = metricRegistry.getExecutions();

		Execution execution2 = executions.get("UserService.getUser.execution");
		assertNotNull(execution2);
		assertTrue(execution == execution2);

		Execution execution3 = metricRegistry.execution(MetricRegistry.name("UserService", "getUser.execution"));
		assertNotNull(execution3);
		assertTrue(execution == execution3);
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

		assertEquals(60, metric.pcts.get(60d), 0);
		assertEquals(70, metric.pcts.get(70d), 0);

		// default 90
		Histogram histogramWithDefaultPcts = metricRegistry.histogram(MetricRegistry.name("UserService",
				"getUser.histogram.default"));
		for (int i = 1; i <= 100; i++) {
			histogramWithDefaultPcts.update(i);
		}

		metric = histogramWithDefaultPcts.calculateMetric();
		assertEquals(90, metric.pcts.get(90d), 0);

		// new default 50
		metricRegistry.setDefaultPcts(new Double[] { 50d });
		Histogram histogramWithNewDefaultPcts = metricRegistry.histogram(MetricRegistry.name("UserService",
				"getUser.histogram.newDefault"));

		for (int i = 1; i <= 100; i++) {
			histogramWithNewDefaultPcts.update(i);
		}

		metric = histogramWithNewDefaultPcts.calculateMetric();

		assertEquals(50, metric.pcts.get(50d), 0);
		assertNull(metric.pcts.get(90d));
	}
}
