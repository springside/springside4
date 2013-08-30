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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.nosql.redis.JedisScriptExecutor;
import org.springside.modules.nosql.redis.JedisTemplate;
import org.springside.modules.utils.Threads;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.google.common.collect.Lists;

public class BatchJobConsumer {
	public static final int DEFAULT_CONNECTION_RETRY_MILLS = 5000;
	public static final String DEFAULT_LUA_FILE_PATH = "classpath:/redis/batchpop.lua";

	public static final boolean DEFAULT_RELIABLE = false;
	public static final int DEFAULT_BATCH_SIZE = 1;

	private static Logger logger = LoggerFactory.getLogger(BatchJobConsumer.class);

	private boolean reliable = DEFAULT_RELIABLE;
	private int batchSize = DEFAULT_BATCH_SIZE;

	private JedisTemplate jedisTemplate;
	private JedisScriptExecutor scriptExecutor;

	private String scriptPath = DEFAULT_LUA_FILE_PATH;
	private String readyJobKey;
	private String lockJobKey;
	private List<String> keys;

	public BatchJobConsumer(String jobName, JedisPool jedisPool) {
		readyJobKey = Keys.getReadyJobKey(jobName);
		lockJobKey = Keys.getLockJobKey(jobName);
		keys = Lists.newArrayList(readyJobKey, lockJobKey);

		jedisTemplate = new JedisTemplate(jedisPool);
		scriptExecutor = new JedisScriptExecutor(jedisPool);
	}

	public void init() {
		scriptExecutor.loadFromFile(scriptPath);
	}

	public List<String> popupJobs() {
		List<String> jobs = null;
		try {
			long currTime = System.currentTimeMillis();
			List<String> args = Lists.newArrayList(String.valueOf(currTime), String.valueOf(batchSize),
					String.valueOf(reliable));
			jobs = (List<String>) scriptExecutor.execute(keys, args);
		} catch (JedisConnectionException e) {
			Threads.sleep(DEFAULT_CONNECTION_RETRY_MILLS);
		}

		return jobs;
	}

	/**
	 * 在高可靠模式下，报告任务完成, 从lock table set中删除.
	 */
	public void ackJob(String job) {
		jedisTemplate.zrem(lockJobKey, job);
	}

	/**
	 * 设置不在默认路径的lua script path，按Spring Resource的URL格式.
	 */
	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}

	/**
	 * 设置是否高可靠模式。
	 */
	public void setReliable(boolean reliable) {
		this.reliable = reliable;
	}

	/**
	 * 设置批量取回任务数量.
	 */
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
}
