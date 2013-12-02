package org.springside.modules.metrics;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springside.modules.metrics.utils.Clock.MockedDateProvider;

public class CounterTest {

	@Test
	public void normal() {
		MockedDateProvider clock = new MockedDateProvider(0);
		Counter counter = new Counter(clock);

		counter.inc(10);
		counter.inc(20);
		counter.inc(30);
		clock.incrementTime(1000);

		CounterMetric metric = counter.getMetric();
		assertEquals(60, metric.count);
		assertEquals(60d, metric.lastRate, 0);
		assertEquals(60d, metric.meanRate, 0);

		counter.inc(20);
		clock.incrementTime(1000);
		metric = counter.getMetric();

		assertEquals(80, metric.count);
		assertEquals(20d, metric.lastRate, 0);
		assertEquals(40d, metric.meanRate, 0);
	}

	@Test
	public void incAndDec() {
		MockedDateProvider clock = new MockedDateProvider(0);
		Counter counter = new Counter(clock);

		counter.inc(20);
		counter.inc();
		counter.inc();
		counter.dec(10);
		counter.dec();
		clock.incrementTime(1000);

		CounterMetric metric = counter.getMetric();
		assertEquals(11, metric.count);
	}
}
