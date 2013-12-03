package org.springside.modules.metrics;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springside.modules.metrics.utils.Clock.MockedClock;

public class CounterTest {

	@Test
	public void normal() {
		MockedClock clock = new MockedClock();
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
		MockedClock clock = new MockedClock();
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

	@Test
	public void reset() {
		MockedClock clock = new MockedClock();
		Counter counter = new Counter(clock);

		counter.inc(20);
		clock.incrementTime(1000);

		CounterMetric metric = counter.getMetric();
		assertEquals(20, metric.count);
		assertEquals(20d, metric.lastRate, 0);
		assertEquals(20d, metric.meanRate, 0);

		counter.reset();
		counter.inc(30);
		clock.incrementTime(1000);

		metric = counter.getMetric();
		assertEquals(30, metric.count);
		assertEquals(30d, metric.lastRate, 0);
		assertEquals(30d, metric.meanRate, 0);
	}

	@Test
	public void empty() {
		MockedClock clock = new MockedClock();
		Counter counter = new Counter(clock);
		clock.incrementTime(1000);

		CounterMetric metric = counter.getMetric();
		assertEquals(0, metric.count);
		assertEquals(0d, metric.lastRate, 0);
		assertEquals(0d, metric.meanRate, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkArgument() {
		Counter counter = new Counter(null);
	}
}
