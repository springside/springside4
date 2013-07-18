package org.springside.modules.nosql.redis.scheduler;

public class Keys {

	public static String getSleepingJobKey(String jobName) {
		return new StringBuilder().append("job:").append(jobName).append(":sleepingjob").toString();
	}

	public static String getReadyJobKey(String jobName) {
		return new StringBuilder().append("job:").append(jobName).append(":readyjob").toString();
	}

	public static String getDispatchCounterKey(String jobName) {
		return new StringBuilder().append("job:").append(jobName).append(":counter").toString();
	}
}
