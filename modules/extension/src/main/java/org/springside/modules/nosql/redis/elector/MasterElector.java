/*------------------------------------------------------------------------------
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
package org.springside.modules.nosql.redis.elector;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.nosql.redis.JedisTemplate;
import org.springside.modules.nosql.redis.JedisTemplate.JedisActionNoResult;
import org.springside.modules.utils.Threads;
import org.springside.modules.utils.Threads.WrapExceptionRunnable;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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

	private static AtomicInteger poolNumber = new AtomicInteger(1);
	private ScheduledExecutorService internalScheduledThreadPool;
	private ScheduledFuture electorJob;
	private int intervalSecs;
	private int expireSecs;

	private JedisTemplate jedisTemplate;

	private String hostId;
	private String masterKey = DEFAULT_MASTER_KEY;
	private AtomicBoolean master = new AtomicBoolean(false);

	public MasterElector(JedisPool jedisPool, int intervalSecs) {
		jedisTemplate = new JedisTemplate(jedisPool);
		this.intervalSecs = intervalSecs;
		this.expireSecs = intervalSecs + (intervalSecs / 2);
	}

	/**
	 * 返回目前该实例是否master。
	 */
	public boolean isMaster() {
		return master.get();
	}

	/**
	 * 启动抢注线程, 自行创建scheduler线程池.
	 */
	public void start() {
		internalScheduledThreadPool = Executors.newScheduledThreadPool(1,
				Threads.buildJobFactory("Master-Elector-" + poolNumber.getAndIncrement() + "-%d"));
		start(internalScheduledThreadPool);
	}

	/**
	 * 启动抢注线程, 使用传入的scheduler线程池.
	 */
	public void start(ScheduledExecutorService scheduledThreadPool) {
		hostId = generateHostId();
		electorJob = scheduledThreadPool.scheduleAtFixedRate(new WrapExceptionRunnable(this), 0, intervalSecs,
				TimeUnit.SECONDS);
		logger.info("masterElector start, hostName:{}.", hostId);
	}

	/**
	 * 停止抢注线程，如果是自行创建的threadPool则自行销毁。
	 * 如果是master, 则主动注销key.
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
	 * 生成host id的方法哦，可在子类重载.
	 */
	protected String generateHostId() {
		String host = "localhost";
		try {
			host = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logger.warn("can not get hostName", e);
		}
		host = host + "-" + new SecureRandom().nextInt(10000);

		return host;
	}

	@Override
	public void run() {
		jedisTemplate.execute(new JedisActionNoResult() {// NOSONAR
					@Override
					public void action(Jedis jedis) {
						String masterFromRedis = jedis.get(masterKey);

						logger.debug("master is {}", masterFromRedis);

						// if master is null, the cluster just start or the master had crashed, try to register myself
						// as master
						if (masterFromRedis == null) {
							// use setnx to make sure only one client can register as master.
							if (jedis.setnx(masterKey, hostId) > 0) {
								// 等jedis支持2.6.12开始的新的set语法，就可以不用再担心setnx后expire还没来得及执行就crash。
								jedis.expire(masterKey, expireSecs);
								master.set(true);

								logger.info("master is changed to {}.", hostId);
								return;
							} else {
								master.set(false);
								return;
							}
						}

						// if master is myself, update the expire time.
						if (hostId.equals(masterFromRedis)) {
							jedis.expire(masterKey, expireSecs);
							master.set(true);
						} else {
							// 在jedis支持2.6.12开始的新的set语法前，如果我不是master，以在野党的身份检查一下master key上的expire是否已设置。
							if (jedis.ttl(masterKey) == -1) {
								jedis.expire(masterKey, expireSecs);
								logger.info("master key doesn't has expired time, set it again");
							}
							master.set(false);
						}
					}
				});
	}

	/**
	 * 如果应用中有多种master，设置唯一的master name。
	 */
	public void setMasterKey(String masterKey) {
		this.masterKey = masterKey;
	}

	// for test
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
}
