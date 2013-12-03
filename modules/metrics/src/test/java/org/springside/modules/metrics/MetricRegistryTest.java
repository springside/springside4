package org.springside.modules.metrics;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;
import org.springside.modules.metrics.utils.Clock.MockedClock;

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
	public void updateDefault() {
		// default clock
		MockedClock clock = new MockedClock();
		MetricRegistry metricRegistry = new MetricRegistry();
		metricRegistry.setDefaultClock(clock);

		Counter counter = metricRegistry.counter(MetricRegistry.name("UserService", "getUser.new.counter"));
		counter.inc(100000);
		clock.incrementTime(50000);

		assertEquals(2000, counter.getMetric().lastRate, 1);

		// default pcts
		metricRegistry.setDefaultPcts(new Double[] { 0.5 });
		Histogram histogram = metricRegistry.histogram(MetricRegistry.name("UserService", "getUser.new.histogram"));

		for (int i = 1; i <= 100; i++) {
			histogram.update(i);
		}

		HistogramMetric metric = histogram.getMetric();

		assertEquals(50, metric.pcts.get(0.5), 0);
		assertNull(metric.pcts.get(0.9));
	}
}
