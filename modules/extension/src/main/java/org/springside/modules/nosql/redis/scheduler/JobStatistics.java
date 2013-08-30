package org.springside.modules.nosql.redis.scheduler;

import org.springside.modules.nosql.redis.JedisTemplate;

import redis.clients.jedis.JedisPool;

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
	 * 获取未达到触发条件进行分发的Job数量.
	 */
	public long getScheduledJobNumber() {
		return jedisTemplate.zcard(scheduledJobKey);
	}

	/**
	 * 获取已分发但未被执行的Job数量.
	 */
	public long getReadyJobNumber() {
		return jedisTemplate.llen(readyJobKey);
	}

	/**
	 * 获取高可靠模式下已被取走执行但未报告完成的Job数量.
	 */
	public long getLockJobNumber() {
		return jedisTemplate.zcard(lockJobKey);
	}

	/**
	 * 获取已分发的Job总数。
	 */
	public long getDispatchCounter() {
		return jedisTemplate.getAsLong(dispatchCounterKey);
	}

	/**
	 * 获取已重做的Job总数。
	 */
	public long getRetryCounter() {
		return jedisTemplate.getAsLong(retryCounterKey);
	}

	/**
	 * 重置已分发的Job计数器和已重做的计数器.
	 */
	public void restCounters() {
		jedisTemplate.set(dispatchCounterKey, "0");
		jedisTemplate.set(retryCounterKey, "0");
	}
}
