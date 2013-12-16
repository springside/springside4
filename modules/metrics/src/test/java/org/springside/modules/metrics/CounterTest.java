package org.springside.modules.metrics;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springside.modules.metrics.utils.Clock.MockClock;

public class CounterTest {

	private MockClock clock = new MockClock();

	@Before
	public void setup() {
		Counter.clock = clock;
	}

	@Test
	public void normal() {

		Counter counter = new Counter();
		counter.inc(10);
		counter.inc(20);
		counter.inc(30);
		clock.increaseTime(1000);

		CounterMetric metric = counter.getMetric();
		assertEquals(60, metric.count);
		assertEquals(60d, metric.lastRate, 0);

		counter.inc(20);
		clock.increaseTime(1000);
		metric = counter.getMetric();

		assertEquals(80, metric.count);
		assertEquals(20d, metric.lastRate, 0);
	}

	@Test
	public void incAndDec() {
		Counter counter = new Counter();

		counter.inc(20);
		counter.inc();
		counter.inc();
		counter.dec(10);
		counter.dec();
		clock.increaseTime(1000);

		CounterMetric metric = counter.getMetric();
		assertEquals(11, metric.count);
	}

	@Test
	public void reset() {
		Counter counter = new Counter();

		counter.inc(20);
		clock.increaseTime(1000);

		CounterMetric metric = counter.getMetric();
		assertEquals(20, metric.count);
		assertEquals(20d, metric.lastRate, 0);

		counter.reset();
		counter.inc(30);
		clock.increaseTime(1000);

		metric = counter.getMetric();
		assertEquals(30, metric.count);
		assertEquals(30d, metric.lastRate, 0);
	}

	@Test
	public void empty() {
		Counter counter = new Counter();
		clock.increaseTime(1000);

		CounterMetric metric = counter.getMetric();
		assertEquals(0, metric.count);
		assertEquals(0d, metric.lastRate, 0);
	}
}
