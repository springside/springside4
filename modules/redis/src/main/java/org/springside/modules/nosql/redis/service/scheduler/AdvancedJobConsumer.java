/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.nosql.redis.service.scheduler;

import java.util.List;

import org.springside.modules.nosql.redis.JedisScriptExecutor;
import org.springside.modules.nosql.redis.JedisTemplate;
import org.springside.modules.nosql.redis.pool.JedisPool;
import org.springside.modules.utils.Threads;

import redis.clients.jedis.exceptions.JedisConnectionException;

import com.google.common.collect.Lists;

/**
 * 高级的使用Lua脚本取回任务，支持高可靠性和批量取回任务，但不会阻塞，如果没有任务即时返回。
 * 
 * 在高可靠模式下，任务在返回给客户端的同时，会放入lock table中，客户完成任务后必须调用ack()删除任务，否则Dispatcher会将超时未完成的任务放入队列重新执行.
 * 
 * @author calvin
 */
public class AdvancedJobConsumer {
	public static final String DEFAULT_BATCH_POP_LUA_FILE_PATH = "classpath:/redis/batchpop.lua";
	public static final String DEFAULT_SINGLE_POP_LUA_FILE_PATH = "classpath:/redis/singlepop.lua";

	public static final int DEFAULT_CONNECTION_RETRY_MILLS = 5000;
	public static final boolean DEFAULT_RELIABLE = false;
	public static final int DEFAULT_BATCH_SIZE = 10;

	private boolean reliable = DEFAULT_RELIABLE;
	private int batchSize = DEFAULT_BATCH_SIZE;

	private JedisTemplate jedisTemplate;
	private JedisScriptExecutor singlePopScriptExecutor;
	private JedisScriptExecutor batchPopScriptExecutor;

	private String batchPopScriptPath = DEFAULT_BATCH_POP_LUA_FILE_PATH;
	private String singlePopScriptPath = DEFAULT_SINGLE_POP_LUA_FILE_PATH;

	private String readyJobKey;
	private String lockJobKey;
	private List<String> keys;

	public AdvancedJobConsumer(String jobName, JedisPool jedisPool) {
		readyJobKey = Keys.getReadyJobKey(jobName);
		lockJobKey = Keys.getLockJobKey(jobName);
		keys = Lists.newArrayList(readyJobKey, lockJobKey);

		jedisTemplate = new JedisTemplate(jedisPool);
		singlePopScriptExecutor = new JedisScriptExecutor(jedisPool);
		batchPopScriptExecutor = new JedisScriptExecutor(jedisPool);
	}

	/**
	 * 初始化脚本，在popup前必须被调用.
	 */
	public void init() {
		singlePopScriptExecutor.loadFromFile(singlePopScriptPath);
		batchPopScriptExecutor.loadFromFile(batchPopScriptPath);
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
			List<String> args = Lists.newArrayList(String.valueOf(currTime), String.valueOf(reliable));
			job = (String) singlePopScriptExecutor.execute(keys, args);
		} catch (JedisConnectionException e) {
			Threads.sleep(DEFAULT_CONNECTION_RETRY_MILLS);
		}

		return job;
	}

	/**
	 * 即时批量跑回任务, 如有任务返回的同时将其放入lock job set，如无任务返回空的List.
	 * 如发生redis连接异常, 线程会sleep 5秒后返回null，
	 * 如果发生redis数据错误如lua脚本错误，抛出异常.
	 */
	public List<String> popupJobs() {
		List<String> jobs = null;
		try {
			long currTime = System.currentTimeMillis();
			List<String> args = Lists.newArrayList(String.valueOf(currTime), String.valueOf(batchSize),
					String.valueOf(reliable));
			jobs = (List<String>) batchPopScriptExecutor.execute(keys, args);
		} catch (JedisConnectionException e) {
			Threads.sleep(DEFAULT_CONNECTION_RETRY_MILLS);
		}

		return jobs;
	}

	/**
	 * 在高可靠模式下，报告任务完成, 从lock table set中删除任务.
	 */
	public void ackJob(String job) {
		jedisTemplate.zrem(lockJobKey, job);
	}

	/**
	 * 设置不在默认路径的lua script path，按Spring Resource的URL格式.
	 */
	public void setBatchPopScriptPath(String batchPopScriptPath) {
		this.batchPopScriptPath = batchPopScriptPath;
	}

	/**
	 * 设置不在默认路径的lua script path，按Spring Resource的URL格式.
	 */
	public void setSinglePopScriptPath(String singlePopScriptPath) {
		this.singlePopScriptPath = singlePopScriptPath;
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
