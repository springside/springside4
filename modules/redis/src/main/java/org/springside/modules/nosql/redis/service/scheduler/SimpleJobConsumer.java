/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.nosql.redis.service.scheduler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.nosql.redis.JedisTemplate;
import org.springside.modules.nosql.redis.JedisTemplate.JedisAction;
import org.springside.modules.nosql.redis.pool.JedisPool;
import org.springside.modules.utils.Threads;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

/*
 * 简单的基于brpop()API, 阻塞的取出任务。
 * brpop的阻塞，在线程中断时不会自动退出，所以还是设置有限timeout时间，另外在线程池退出时已比timeout时间长的时间调用awaitTermination()等待线程结束.
 */
public class SimpleJobConsumer {

	public static final int DEFAULT_POPUP_TIMEOUT_SECONDS = 5;
	public static final int DEFAULT_CONNECTION_RETRY_MILLS = 5000;

	private static Logger logger = LoggerFactory.getLogger(SimpleJobConsumer.class);

	private JedisTemplate jedisTemplate;
	private String readyJobKey;
	private int popupTimeoutSecs = DEFAULT_POPUP_TIMEOUT_SECONDS;

	public SimpleJobConsumer(String jobName, JedisPool jedisPool) {
		jedisTemplate = new JedisTemplate(jedisPool);
		readyJobKey = Keys.getReadyJobKey(jobName);
	}

	/**
	 * 阻塞直到返回任务，如果popupTimeoutSecs内(默认5秒)无任务到达，返回null.
	 * 如发生redis连接异常, 线程会sleep 5秒后返回null，
	 */
	public String popupJob() {

		List<String> nameValuePair = null;
		try {
			nameValuePair = jedisTemplate.execute(new JedisAction<List<String>>() {
				@Override
				public List<String> action(Jedis jedis) {
					return jedis.brpop(popupTimeoutSecs, readyJobKey);
				}
			});
		} catch (JedisConnectionException e) {
			Threads.sleep(DEFAULT_CONNECTION_RETRY_MILLS);
		}

		if ((nameValuePair != null) && !nameValuePair.isEmpty()) {
			return nameValuePair.get(1);
		} else {
			return null;
		}
	}

	public void setPopupTimeoutSecs(int popupTimeoutSecs) {
		this.popupTimeoutSecs = popupTimeoutSecs;
	}
}
