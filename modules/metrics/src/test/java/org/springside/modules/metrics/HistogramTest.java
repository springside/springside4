package org.springside.modules.metrics;

import static org.junit.Assert.*;

import org.junit.Test;

public class HistogramTest {

	@Test
	public void normal() {
		Histogram histogram = new Histogram(new Double[] { 0.9, 0.95 });

		for (int i = 1; i <= 100; i++) {
			histogram.update(i);
		}

		HistogramMetric metric = histogram.getMetric();

		assertEquals(1, metric.min);
		assertEquals(100, metric.max);
		assertEquals(50.5, metric.mean, 0);
		assertEquals(90, metric.pcts.get(0.9), 0);
		assertEquals(95, metric.pcts.get(0.95), 0);

		for (int i = 1; i <= 100; i++) {
			histogram.update(i * 2);
		}

		metric = histogram.getMetric();

		assertEquals(2, metric.min);
		assertEquals(200, metric.max);
		assertEquals(101, metric.mean, 0);
		assertEquals(180, metric.pcts.get(0.9), 0);
		assertEquals(190, metric.pcts.get(0.95), 0);
	}

	@Test
	public void fewData() {
		Histogram histogram = new Histogram(new Double[] { 0.9, 0.95 });

		histogram.update(1);
		HistogramMetric metric = histogram.getMetric();
		assertEquals(1, metric.pcts.get(0.9), 0);
		assertEquals(1, metric.pcts.get(0.95), 0);

		for (int i = 1; i <= 3; i++) {
			histogram.update(i);
		}
		metric = histogram.getMetric();

		assertEquals(1, metric.min);
		assertEquals(3, metric.max);
		assertEquals(2, metric.mean, 0);
		assertEquals(3, metric.pcts.get(0.9), 0);
		assertEquals(3, metric.pcts.get(0.95), 0);
	}

	@Test
	public void emptyMesures() {
		Histogram histogram = new Histogram(new Double[] { 0.9, 0.95 });

		HistogramMetric metric = histogram.getMetric();

		assertEquals(0, metric.min);
		assertEquals(0, metric.max);
		assertEquals(0, metric.mean, 0);
		assertEquals(0, metric.pcts.get(0.9), 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkArgument() {
		Histogram histogram = new Histogram(null);
	}

	@Test()
	public void emptyPcts() {
		Histogram histogram = new Histogram(new Double[] {});
		for (int i = 1; i <= 3; i++) {
			histogram.update(i);
		}

		HistogramMetric metric = histogram.getMetric();
		assertEquals(3, metric.max);
		assertTrue(metric.pcts.isEmpty());
	}
}
