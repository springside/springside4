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

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.nosql.redis.JedisTemplate;
import org.springside.modules.nosql.redis.JedisTemplate.JedisAction;
import org.springside.modules.nosql.redis.JedisTemplate.JedisActionNoResult;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * This is the Redis implementation of SchedulerManager.
 */
public class JobManager {

	private static final int REDIS_READ_TIMEOUT = 5;

	private static Logger logger = LoggerFactory.getLogger(JobManager.class);

	private String sleepingJobName;
	private String readyJobName;

	private JedisTemplate jedisTemplate = null;

	public JobManager(String jobName, JedisPool jedisPool) {
		jedisTemplate = new JedisTemplate(jedisPool);

		sleepingJobName = jobName + ".job:sleeping";
		readyJobName = jobName + ".job:ready";
	}

	public void scheduleJob(final String job, long delay, TimeUnit timeUnit) {
		final long delayTimeInMillisecond = System.currentTimeMillis() + timeUnit.toMillis(delay);
		jedisTemplate.zadd(sleepingJobName, delayTimeInMillisecond, job);
	}

	public boolean cancelJob(final String jobId) {
		long removeNumber = jedisTemplate.execute(new JedisAction<Long>() {
			@Override
			public Long action(Jedis jedis) {
				return jedis.zrem(sleepingJobName, jobId);
			}
		});

		if (removeNumber == 0) {
			logger.warn("Can't not cancel job by id {}", jobId);
			return false;
		}

		return true;
	}

	public void startJobListener(final JobListener jobListener) {

		jedisTemplate.execute(new JedisActionNoResult() {
			@Override
			public void action(Jedis jedis) {
				while (true) {
					List<String> nameValuePair = jedis.brpop(REDIS_READ_TIMEOUT, readyJobName);
					if (!nameValuePair.isEmpty()) {
						String job = nameValuePair.get(1);
						jobListener.receiveJob(job);
					}
				}
			}
		});
	}

	public interface JobListener {
		void receiveJob(String job);
	}
}
