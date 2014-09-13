/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.nosql.redis.service.scheduler;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.nosql.redis.JedisScriptExecutor;
import org.springside.modules.nosql.redis.pool.JedisPool;
import org.springside.modules.utils.Threads;
import org.springside.modules.utils.Threads.WrapExceptionRunnable;

import com.google.common.collect.Lists;

/**
 * 定时分发任务的管理器。
 * 定时从scheduled job sorted set中取出到期的任务放入ready job list，并在高可靠模式下，将lock job 中 已超时的任务重新放入 ready job.
 * 线程池可自行创建，也可以从外部传入共用。
 * 
 * @author calvin
 */
public class JobDispatcher implements Runnable {
	public static final String DEFAULT_DISPATCH_LUA_FILE = "classpath:/redis/dispatch.lua";
	public static final long DEFAULT_INTERVAL_MILLIS = 1000;
	public static final boolean DEFAULT_RELIABLE = false;
	public static final long DEFAULT_JOB_TIMEOUT_SECONDS = 60;

	private static Logger logger = LoggerFactory.getLogger(JobDispatcher.class);

	private ScheduledExecutorService internalScheduledThreadPool;
	private ScheduledFuture dispatchJob;

	private long intervalMillis = DEFAULT_INTERVAL_MILLIS;
	private boolean reliable = DEFAULT_RELIABLE;
	private long jobTimeoutSecs = DEFAULT_JOB_TIMEOUT_SECONDS;

	private JedisScriptExecutor scriptExecutor;
	private String scriptPath = DEFAULT_DISPATCH_LUA_FILE;

	private String jobName;
	private List<String> keys;

	public JobDispatcher(String jobName, JedisPool jedisPool) {
		this.jobName = jobName;

		String scheduledJobKey = Keys.getScheduledJobKey(jobName);
		String readyJobKey = Keys.getReadyJobKey(jobName);
		String dispatchCounterKey = Keys.getDispatchCounterKey(jobName);
		String lockJobKey = Keys.getLockJobKey(jobName);
		String retryCounterKey = Keys.getRetryCounterKey(jobName);

		keys = Lists.newArrayList(scheduledJobKey, readyJobKey, dispatchCounterKey, lockJobKey, retryCounterKey);

		scriptExecutor = new JedisScriptExecutor(jedisPool);
	}

	/**
	 * 启动分发线程, 自行创建scheduler线程池.
	 */
	public void start() {
		internalScheduledThreadPool = Executors.newScheduledThreadPool(1,
				Threads.buildJobFactory("Job-Dispatcher-" + jobName + "-%d"));

		start(internalScheduledThreadPool);
	}

	/**
	 * 启动分发线程, 使用传入的scheduler线程池.
	 */
	public void start(ScheduledExecutorService scheduledThreadPool) {
		scriptExecutor.loadFromFile(scriptPath);

		dispatchJob = scheduledThreadPool.scheduleAtFixedRate(new WrapExceptionRunnable(this), 0, intervalMillis,
				TimeUnit.MILLISECONDS);
	}

	/**
	 * 停止分发任务，如果是自行创建的threadPool则自行销毁，关闭时最多等待5秒。
	 */
	public void stop() {
		dispatchJob.cancel(false);

		if (internalScheduledThreadPool != null) {
			Threads.normalShutdown(internalScheduledThreadPool, 5, TimeUnit.SECONDS);
		}
	}

	/**
	 * 以当前时间为参数执行Lua Script分发任务。
	 */
	@Override
	public void run() {
		try {
			long currTime = System.currentTimeMillis();
			List<String> args = Lists.newArrayList(String.valueOf(currTime), String.valueOf(reliable),
					String.valueOf(jobTimeoutSecs));
			scriptExecutor.execute(keys, args);
		} catch (Throwable e) {
			// catch any exception, because the scheduled thread will break if the exception thrown outside.
			logger.error("Unexpected error occurred in task", e);
		}
	}

	/**
	 * 设置非默认的script path, 格式为spring的Resource路径风格。
	 */
	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}

	/**
	 * 设置非默认1秒的分发间隔.
	 */
	public void setIntervalMillis(long intervalMillis) {
		this.intervalMillis = intervalMillis;
	}

	/**
	 * 设置是否支持高可靠性.
	 */
	public void setReliable(boolean reliable) {
		this.reliable = reliable;
	}

	/**
	 * 设置高可靠性模式下，非默认1分钟的任务执行超时时间。
	 */
	public void setJobTimeoutSecs(long jobTimeoutSecs) {
		this.jobTimeoutSecs = jobTimeoutSecs;
	}
}
