/*------------------------------------------------------------------------------
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
package org.springside.modules.nosql.redis.scheduler;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.nosql.redis.JedisTemplate;

import redis.clients.jedis.JedisPool;

/**
 * 任务管理，支持任务的安排与取消。
 */
public class JobManager {

	private static Logger logger = LoggerFactory.getLogger(JobManager.class);

	private JedisTemplate jedisTemplate;

	private String sleepingJobKey;

	public JobManager(String jobName, JedisPool jedisPool) {
		jedisTemplate = new JedisTemplate(jedisPool);
		sleepingJobKey = Keys.getSleepingJobKey(jobName);
	}

	/**
	 * 安排任务.
	 */
	public void scheduleJob(final String job, final long delay, final TimeUnit timeUnit) {
		final long delayTimeMillis = System.currentTimeMillis() + timeUnit.toMillis(delay);
		jedisTemplate.zadd(sleepingJobKey, job, delayTimeMillis);
	}

	/**
	 * 取消任务, 如果任务不存在或已被触发返回false, 否则返回true.
	 */
	public boolean cancelJob(final String job) {
		boolean removed = jedisTemplate.zrem(sleepingJobKey, job);

		if (!removed) {
			logger.warn("Can't cancel job by value {}", job);
		}

		return removed;
	}
}
