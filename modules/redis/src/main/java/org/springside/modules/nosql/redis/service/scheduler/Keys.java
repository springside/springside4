/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.nosql.redis.service.scheduler;

public class Keys {

	public static String getScheduledJobKey(String jobName) {
		return new StringBuilder().append("job:").append(jobName).append(":scheduled").toString();
	}

	public static String getReadyJobKey(String jobName) {
		return new StringBuilder().append("job:").append(jobName).append(":ready").toString();
	}

	public static String getLockJobKey(String jobName) {
		return new StringBuilder().append("job:").append(jobName).append(":lock").toString();
	}

	public static String getDispatchCounterKey(String jobName) {
		return new StringBuilder().append("job:").append(jobName).append(":dispatch.counter").toString();
	}

	public static String getRetryCounterKey(String jobName) {
		return new StringBuilder().append("job:").append(jobName).append(":retry.counter").toString();
	}
}
