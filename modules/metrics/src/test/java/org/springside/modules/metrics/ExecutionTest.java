/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.springside.modules.metrics.Execution.ExecutionTimer;
import org.springside.modules.metrics.utils.Clock.MockClock;

public class ExecutionTest {

	@Test
	public void normal() {
		MockClock clock = new MockClock();
		Execution.clock = clock;
		Counter.clock = clock;
		Execution execution = new Execution(new Double[] { 90d });

		ExecutionTimer timer = execution.start();
		clock.increaseTime(200);
		timer.stop();

		ExecutionTimer timer2 = execution.start();
		clock.increaseTime(300);
		timer2.stop();

		ExecutionMetric metric = execution.calculateMetric();

		assertThat(metric.counterMetric.totalCount).isEqualTo(2);
		assertThat(metric.counterMetric.lastRate).isEqualTo(4);

		assertThat(metric.histogramMetric.min).isEqualTo(200);
		assertThat(metric.histogramMetric.mean).isEqualTo(250);
		assertThat(metric.histogramMetric.pcts.get(90d)).isEqualTo(300);
	}
}
