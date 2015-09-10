/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics;

public class CounterMetric {
	public long totalCount;
	public long meanRate;
	public long latestCount;
	public long latestRate;

	@Override
	public String toString() {
		return "CounterMetric [totalCount=" + totalCount + ", meanRate=" + meanRate + ", latestCount=" + latestCount
				+ ", latestRate=" + latestRate + "]";
	}
}
