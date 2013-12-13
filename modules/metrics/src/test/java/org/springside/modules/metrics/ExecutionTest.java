package org.springside.modules.metrics;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springside.modules.metrics.Execution.ExecutionTimer;
import org.springside.modules.metrics.utils.Clock.MockedClock;

public class ExecutionTest {

	@Test
	public void normal() {
		MockedClock clock = new MockedClock();
		Execution execution = new Execution(clock, new Double[] { 90d });

		ExecutionTimer timer = execution.start();
		clock.increaseTime(200);
		timer.stop();

		ExecutionTimer timer2 = execution.start();
		clock.increaseTime(300);
		timer2.stop();

		ExecutionMetric metric = execution.getMetric();

		assertEquals(2, metric.counter.count);
		assertEquals(4, metric.counter.lastRate, 0);

		assertEquals(200, metric.histogram.min);
		assertEquals(250, metric.histogram.mean, 0);
		assertEquals(300, metric.histogram.pcts.get(90d), 0);
	}
}
