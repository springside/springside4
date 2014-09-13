/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.nosql.redis.service.elector;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.nosql.redis.JedisTemplate;
import org.springside.modules.nosql.redis.JedisTemplate.JedisActionNoResult;
import org.springside.modules.nosql.redis.JedisUtils;
import org.springside.modules.nosql.redis.pool.JedisPool;
import org.springside.modules.utils.Threads;
import org.springside.modules.utils.Threads.WrapExceptionRunnable;

import redis.clients.jedis.Jedis;

/**
 * Master选举实现, 基于setNx()与expire()两大API.
 * 与每次使用setNx争夺的分布式锁不同，Master用setNX争夺Master Key成功后，会不断的更新key的expireTime，保持自己的master地位，直到自己倒掉了不能再更新为止。
 * 其他Slave会定时检查Master Key是否已过期，如果已过期则重新发起争夺。
 * 
 * 其他服务可以随时调用isMaster()，查询自己是否master, 与MasterElector的内部定时操作是解耦的。
 * 
 * 在最差情况下，可能有两倍的intervalSecs内集群内没有Master。
 * 
 * @author calvin
 */
public class MasterElector implements Runnable {

	public static final String DEFAULT_MASTER_KEY = "master";

	private static Logger logger = LoggerFactory.getLogger(MasterElector.class);

	private ScheduledExecutorService internalScheduledThreadPool;
	private ScheduledFuture electorJob;
	private int intervalSecs;
	private int expireSecs;

	private JedisTemplate jedisTemplate;

	private String hostId;
	private String masterKey = DEFAULT_MASTER_KEY;
	private AtomicBoolean master = new AtomicBoolean(false);

	public MasterElector(JedisPool jedisPool, int intervalSecs) {
		this.jedisTemplate = new JedisTemplate(jedisPool);
		this.intervalSecs = intervalSecs;
		this.expireSecs = intervalSecs + (intervalSecs / 2);
	}

	/**
	 * 返回当前实例是否master。
	 */
	public boolean isMaster() {
		return master.get();
	}

	/**
	 * 启动抢注线程, 自行创建scheduler线程池.
	 */
	public void start() {
		internalScheduledThreadPool = Executors.newScheduledThreadPool(1,
				Threads.buildJobFactory("Master-Elector-" + masterKey + "-%d"));
		start(internalScheduledThreadPool);
	}

	/**
	 * 启动抢注线程, 使用传入的scheduler线程池.
	 */
	public void start(ScheduledExecutorService scheduledThreadPool) {
		hostId = generateHostId();
		electorJob = scheduledThreadPool.scheduleAtFixedRate(new WrapExceptionRunnable(this), 0, intervalSecs,
				TimeUnit.SECONDS);
		logger.info("masterElector for {} start, hostName:{}.", masterKey, hostId);
	}

	/**
	 * 停止抢注线程，
	 * 如果是master, 则主动注销key.
	 * 如果是自行创建的threadPool则自行销毁,最多5秒超时.
	 */
	public void stop() {
		if (master.get()) {
			jedisTemplate.del(masterKey);
		}

		electorJob.cancel(false);

		if (internalScheduledThreadPool != null) {
			Threads.normalShutdown(internalScheduledThreadPool, 5, TimeUnit.SECONDS);
		}
	}

	/**
	 * 生成host id的方法，可在子类重载.
	 */
	protected String generateHostId() {
		String host = "localhost";
		try {
			host = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logger.warn("can not get hostName, use localhost as default.", e);
		}
		host = host + "-" + new SecureRandom().nextInt(10000);

		return host;
	}

	@Override
	public void run() {
		try {
			jedisTemplate.execute(new JedisActionNoResult() {
				@Override
				public void action(Jedis jedis) {
					String masterFromRedis = jedis.get(masterKey);

					logger.debug("master {} is {}", masterKey, masterFromRedis);

					// 如果masterKey返回值为空，证明集群刚重启 或master已crash，尝试注册为Master.
					if (masterFromRedis == null) {
						// 使用setnx，保证只有一个Client能注册为Master.
						if (JedisUtils.isStatusOk(jedis.set(masterKey, hostId, "NX", "EX", expireSecs))) {
							master.set(true);
							logger.info("master {} is changed to {}.", masterKey, hostId);
							return;
						} else {
							master.set(false);
							return;
						}
					}

					// 如果我已是master，更新key的超时时间
					if (hostId.equals(masterFromRedis)) {
						jedis.expire(masterKey, expireSecs);
						master.set(true);
					} else {
						master.set(false);
					}
				}
			});
		} catch (Throwable e) {
			// catch any exception, because the scheduled thread will break if the exception thrown outside.
			logger.error("Unexpected error occurred in task", e);
		}
	}

	/**
	 * 如果应用中有多种master，设置唯一的master name。
	 */
	public void setMasterKey(String masterKey) {
		this.masterKey = masterKey;
	}

	// for test
	void setHostId(String hostId) {
		this.hostId = hostId;
	}
}
