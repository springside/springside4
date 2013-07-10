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

import org.springside.modules.nosql.redis.JedisTemplate;
import org.springside.modules.nosql.redis.JedisTemplate.JedisActionNoResult;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * This is the Redis implementation of SchedulerManager.
 */
public class JobListener implements Runnable {

	public static final int POPUP_TIMEOUT = 5;

	private String readyJobName;

	private JedisTemplate jedisTemplate = null;

	private final JobHandler jobHandler;

	public JobListener(String jobName, JedisPool jedisPool, JobHandler jobHandler) {
		jedisTemplate = new JedisTemplate(jedisPool);
		readyJobName = jobName + ".job:ready";
		this.jobHandler = jobHandler;
	}

	@Override
	public void run() {
		jedisTemplate.execute(new JedisActionNoResult() {
			@Override
			public void action(Jedis jedis) {
				while (!Thread.currentThread().isInterrupted()) {
					List<String> nameValuePair = jedis.brpop(POPUP_TIMEOUT, readyJobName);
					if ((nameValuePair != null) && !nameValuePair.isEmpty()) {
						String job = nameValuePair.get(1);
						jobHandler.receiveJob(job);
					}
				}
			}
		});
	}

	public interface JobHandler {
		void receiveJob(String job);
	}
}
