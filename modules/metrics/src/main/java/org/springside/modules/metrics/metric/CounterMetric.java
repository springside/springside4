/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics.metric;

/**
 * Counter的统计值
 */
public class CounterMetric {
	public long totalCount; //从启动到目前的调用次数
	public long avgRate;    //从启动到目前的平均QPS
	public long latestCount;//最后一个报告周期的调用次数
	public long latestRate; //最后一个报告周期的QPS

	@Override
	public String toString() {
		return "CounterMetric [totalCount=" + totalCount + ", avgRate=" + avgRate + ", latestCount=" + latestCount
				+ ", latestRate=" + latestRate + "]";
	}
}
