/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.nosql.redis.service.scheduler;

import org.springside.modules.nosql.redis.JedisTemplate;
import org.springside.modules.nosql.redis.pool.JedisPool;

/**
 * 支持对当前任务池情况的状态数据查询.
 * 
 * @author calvin
 */
public class JobStatistics {

	private JedisTemplate jedisTemplate;

	private String scheduledJobKey;
	private String readyJobKey;
	private String lockJobKey;
	private String dispatchCounterKey;
	private String retryCounterKey;

	public JobStatistics(String jobName, JedisPool jedisPool) {
		scheduledJobKey = Keys.getScheduledJobKey(jobName);
		readyJobKey = Keys.getReadyJobKey(jobName);
		lockJobKey = Keys.getLockJobKey(jobName);

		dispatchCounterKey = Keys.getDispatchCounterKey(jobName);
		retryCounterKey = Keys.getRetryCounterKey(jobName);

		jedisTemplate = new JedisTemplate(jedisPool);
	}

	/**
	 * 获取已安排但未达到触发条件的Job数量.
	 */
	public long getScheduledJobNumber() {
		return jedisTemplate.zcard(scheduledJobKey);
	}

	/**
	 * 获取已触发但未被客户端取走的Job数量.
	 */
	public long getReadyJobNumber() {
		return jedisTemplate.llen(readyJobKey);
	}

	/**
	 * 获取高可靠模式下，已被取走执行但未报告完成的Job数量.
	 */
	public long getLockJobNumber() {
		return jedisTemplate.zcard(lockJobKey);
	}

	/**
	 * 获取已触发的Job总数。
	 */
	public long getDispatchCounter() {
		return jedisTemplate.getAsLong(dispatchCounterKey);
	}

	/**
	 * 获取高可靠模式下，已重做的Job总数。
	 */
	public long getRetryCounter() {
		return jedisTemplate.getAsLong(retryCounterKey);
	}

	/**
	 * 重置所有计数器.
	 */
	public void restCounters() {
		jedisTemplate.set(dispatchCounterKey, "0");
		jedisTemplate.set(retryCounterKey, "0");
	}
}
