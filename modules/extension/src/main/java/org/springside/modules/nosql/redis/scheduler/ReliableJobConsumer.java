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

public class ReliableJobConsumer {

	public static final int DEFAULT_CONNECTION_RETRY_MILLS = 5000;
	public static final String DEFAULT_LUA_FILE_PATH = "classpath:/redis/reliablepop.lua";

	private static Logger logger = LoggerFactory.getLogger(ReliableJobConsumer.class);

	private JedisTemplate jedisTemplate;
	private JedisScriptExecutor scriptExecutor;

	private String scriptPath = DEFAULT_LUA_FILE_PATH;
	private String readyJobKey;
	private String lockJobKey;
	private List<String> keys;

	public ReliableJobConsumer(String jobName, JedisPool jedisPool) {
		readyJobKey = Keys.getReadyJobKey(jobName);
		lockJobKey = Keys.getLockJobKey(jobName);
		keys = Lists.newArrayList(readyJobKey, lockJobKey);

		jedisTemplate = new JedisTemplate(jedisPool);
		scriptExecutor = new JedisScriptExecutor(jedisPool);
	}

	public void init() {
		scriptExecutor.loadFromFile(scriptPath);
	}

	/**
	 * 即时返回任务, 如有任务返回的同时将其放入lock job set，如无任务返回null.
	 * 如发生redis连接异常, 线程会sleep 5秒后返回null，
	 * 如果发生redis数据错误如lua脚本错误，抛出异常.
	 */
	public String popupJob() {
		String job = null;
		try {
			long currTime = System.currentTimeMillis();
			List<String> args = Lists.newArrayList(String.valueOf(currTime));
			job = (String) scriptExecutor.execute(keys, args);
		} catch (JedisConnectionException e) {
			Threads.sleep(DEFAULT_CONNECTION_RETRY_MILLS);
		}

		return job;
	}

	/**
	 * 报告任务完成, 从lock table set中删除.
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
}
