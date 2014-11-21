/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics;

import static org.assertj.core.api.Assertions.*;

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

		CounterMetric metric = counter.calculateMetric();
		assertThat(metric.totalCount).isEqualTo(60);
		assertThat(metric.meanRate).isEqualTo(60);
		assertThat(metric.latestCount).isEqualTo(60);
		assertThat(metric.latestRate).isEqualTo(60);

		counter.inc(20);
		clock.increaseTime(1000);
		metric = counter.calculateMetric();

		assertThat(metric.totalCount).isEqualTo(80);
		assertThat(metric.meanRate).isEqualTo(40);
		assertThat(metric.latestCount).isEqualTo(20);
		assertThat(metric.latestRate).isEqualTo(20);
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

		CounterMetric metric = counter.calculateMetric();
		assertThat(metric.totalCount).isEqualTo(11);
	}

	@Test
	public void empty() {
		Counter counter = new Counter();
		clock.increaseTime(1000);

		CounterMetric metric = counter.calculateMetric();
		assertThat(metric.totalCount).isEqualTo(0);
		assertThat(metric.latestRate).isEqualTo(0);
	}
}
