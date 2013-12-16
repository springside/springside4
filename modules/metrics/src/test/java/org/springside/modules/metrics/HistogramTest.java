package org.springside.modules.metrics;

import static org.junit.Assert.*;

import org.junit.Test;

public class HistogramTest {

	@Test
	public void normal() {
		Histogram histogram = new Histogram(90d, 95d);

		for (int i = 1; i <= 100; i++) {
			histogram.update(i);
		}

		HistogramMetric metric = histogram.getMetric();

		assertEquals(1, metric.min);
		assertEquals(100, metric.max);
		assertEquals(50.5, metric.mean, 0);
		assertEquals(90, metric.pcts.get(90d), 0);
		assertEquals(95, metric.pcts.get(95d), 0);

		for (int i = 1; i <= 100; i++) {
			histogram.update(i * 2);
		}

		metric = histogram.getMetric();

		assertEquals(2, metric.min);
		assertEquals(200, metric.max);
		assertEquals(101, metric.mean, 0);
		assertEquals(180, metric.pcts.get(90d), 0);
		assertEquals(190, metric.pcts.get(95d), 0);
	}

	@Test
	public void fewData() {
		Histogram histogram = new Histogram(90d, 95d);

		histogram.update(1);
		HistogramMetric metric = histogram.getMetric();
		assertEquals(1, metric.pcts.get(90d), 0);
		assertEquals(1, metric.pcts.get(95d), 0);

		for (int i = 1; i <= 3; i++) {
			histogram.update(i);
		}
		metric = histogram.getMetric();

		assertEquals(1, metric.min);
		assertEquals(3, metric.max);
		assertEquals(2, metric.mean, 0);
		assertEquals(3, metric.pcts.get(90d), 0);
		assertEquals(3, metric.pcts.get(95d), 0);
	}

	@Test
	public void emptyMesures() {
		Histogram histogram = new Histogram(90d, 95d);

		HistogramMetric metric = histogram.getMetric();

		assertEquals(0, metric.min);
		assertEquals(0, metric.max);
		assertEquals(0, metric.mean, 0);
		assertEquals(0, metric.pcts.get(90d), 0);
	}

	@Test()
	public void emptyPcts() {
		Histogram histogram = new Histogram();
		for (int i = 1; i <= 3; i++) {
			histogram.update(i);
		}

		HistogramMetric metric = histogram.getMetric();
		assertEquals(3, metric.max);
		assertTrue(metric.pcts.isEmpty());
		assertNull(metric.pcts.get(90d));
	}
}
