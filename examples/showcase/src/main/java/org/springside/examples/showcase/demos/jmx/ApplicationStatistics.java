/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.demos.jmx;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * 应用的运行情况统计bean, 通过JMX可被外部监控系统获取数据.
 * 
 * @author calvin
 */
@ManagedResource(objectName = ApplicationStatistics.MBEAN_NAME, description = "Application Statistics Management Bean")
public class ApplicationStatistics {

	public static final String MBEAN_NAME = "showcase:name=ApplicationStatistics";

	private final AtomicInteger listUserTimes = new AtomicInteger();
	private final AtomicInteger updateUserTimes = new AtomicInteger();

	public void incrListUserTimes() {
		listUserTimes.incrementAndGet();
	}

	public void incrUpdateUserTimes() {
		updateUserTimes.incrementAndGet();
	}

	@ManagedAttribute(description = "Times of users be listed")
	public int getListUserTimes() {
		return listUserTimes.get();
	}

	@ManagedAttribute(description = "Times of users be updated")
	public int getUpdateUserTimes() {
		return updateUserTimes.get();
	}

	@ManagedOperation(description = "Reset all statistics")
	public void resetStatistics() {
		listUserTimes.set(0);
		updateUserTimes.set(0);
	}
}
