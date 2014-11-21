/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.nosql.redis.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.nosql.redis.JedisTemplate;
import org.springside.modules.nosql.redis.JedisTemplate.JedisAction;
import org.springside.modules.nosql.redis.JedisUtils;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Pool which to get redis master address get from sentinel instances.
 */
/**
 * Pool which to get redis master address get from sentinel instances.
 */
public final class JedisSentinelPool extends JedisPool {

	private static final String NO_ADDRESS_YET = "I dont know because no sentinel up";

	private static Logger logger = LoggerFactory.getLogger(JedisSentinelPool.class);

	private List<JedisPool> sentinelPools = new ArrayList<JedisPool>();
	private MasterSwitchListener masterSwitchListener;

	private String masterName;
	private JedisPoolConfig masterPoolConfig;
	private ConnectionInfo masterConnectionInfo;
	private CountDownLatch poolInit = new CountDownLatch(1);

	/**
	 * Creates a new instance of <code>JedisSentinelPool</code>.
	 * 
	 * All parameters can be null or empty.
	 * 
	 * @param sentinelAddresses Array of HostAndPort to sentinel instances.
	 * @param masterName One sentinel can monitor several redis master-slave pair, use master name to identify them.
	 * @param masterConnectionInfo The the other information like password,timeout.
	 * @param masterPoolConfig Configuration of redis pool.
	 * 
	 */
	public JedisSentinelPool(String poolName, HostAndPort[] sentinelAddresses, String masterName,
			ConnectionInfo masterConnectionInfo, JedisPoolConfig masterPoolConfig) {

		this.poolName = poolName;
		// sentinelAddresses
		assertArgument(((sentinelAddresses == null) || (sentinelAddresses.length == 0)), "seintinelInfos is not set");

		for (HostAndPort sentinelAddress : sentinelAddresses) {
			JedisPool sentinelPool = new JedisDirectPool(poolName + "-sentinel", sentinelAddress, new JedisPoolConfig());
			sentinelPools.add(sentinelPool);
		}

		// masterConnectionInfo
		assertArgument(masterConnectionInfo == null, "masterConnectionInfo is not set");
		this.masterConnectionInfo = masterConnectionInfo;

		// masterName
		assertArgument(((masterName == null) || masterName.isEmpty()), "masterName is not set");
		this.masterName = masterName;

		// poolConfig
		assertArgument(masterPoolConfig == null, "masterPoolConfig is not set");
		this.masterPoolConfig = masterPoolConfig;

		// Start MasterSwitchListener thread ,internal poll will be start in the thread
		masterSwitchListener = new MasterSwitchListener();
		masterSwitchListener.start();

		try {
			if (!poolInit.await(5, TimeUnit.SECONDS)) {
				logger.warn("the sentiel pool can't not init in 5 seconds");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public JedisSentinelPool(String poolName, HostAndPort[] sentinelAddresses, String masterName,
			JedisPoolConfig masterPoolConfig) {
		this(poolName, sentinelAddresses, masterName, new ConnectionInfo(), masterPoolConfig);
	}

	@Override
	public void destroy() {
		// shutdown the listener thread
		masterSwitchListener.shutdown();

		// destroy sentinel pools
		for (JedisPool sentinel : sentinelPools) {
			sentinel.destroy();
		}

		// destroy redis pool
		destroyInternelPool();

		// wait for the masterSwitchListener thread finish
		try {
			logger.info("Waiting for MasterSwitchListener thread finish");
			masterSwitchListener.join();
			logger.info("MasterSwitchListener thread finished");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	protected void destroyInternelPool() {
		closeInternalPool();

		address = null;
		connectionInfo = null;
		internalPool = null;
	}

	/**
	 * Assert the argurment, throw IllegalArgumentException if the expression is true.
	 */
	private static void assertArgument(boolean expression, String message) {
		if (expression) {
			throw new IllegalArgumentException(message);
		}
	}

	// for test
	public MasterSwitchListener getMasterSwitchListener() {
		return masterSwitchListener;
	}

	/**
	 * Listener thread to listen master switch message from sentinel.
	 */
	public class MasterSwitchListener extends Thread {
		public static final String THREAD_NAME_PREFIX = "MasterSwitchListener-";

		private JedisPubSub subscriber;
		private JedisPool sentinelPool;
		private Jedis sentinelJedis;
		private AtomicBoolean running = new AtomicBoolean(true);
		private HostAndPort previousMasterAddress;

		public MasterSwitchListener() {
			super(THREAD_NAME_PREFIX + masterName);
		}

		// stop the blocking subscription and interrupt the thread
		public void shutdown() {
			// interrupt the thread
			running.getAndSet(false);
			this.interrupt();

			// stop the blocking subscription
			try {
				if (subscriber != null) {
					subscriber.unsubscribe();
				}
			} finally {
				JedisUtils.destroyJedis(sentinelJedis);
			}
		}

		@Override
		public void run() {
			while (running.get()) {
				try {
					sentinelPool = pickupSentinel();

					if (sentinelPool != null) {

						HostAndPort masterAddress = queryMasterAddress();

						if ((internalPool != null) && isAddressChange(masterAddress)) {
							logger.info("The internalPool {} had changed, destroy it now.", previousMasterAddress);
							destroyInternelPool();
						}

						if (internalPool == null) {
							logger.info("The internalPool {} is not init or the address had changed, init it now.",
									masterAddress);
							initInternalPool(masterAddress, masterConnectionInfo, masterPoolConfig);
							poolInit.countDown();
						}

						previousMasterAddress = masterAddress;

						sentinelJedis = sentinelPool.getResource();
						subscriber = new MasterSwitchSubscriber();
						sentinelJedis.subscribe(subscriber, "+switch-master", "+redirect-to-master");
					} else {
						logger.info("All sentinels down, sleep 2 seconds and try to connect again.");
						// When the system startup but the sentinels not yet, init a urgly address to prevent null point
						// exception. change the logic later.
						if (internalPool == null) {
							HostAndPort masterAddress = new HostAndPort(NO_ADDRESS_YET, 6379);
							initInternalPool(masterAddress, masterConnectionInfo, masterPoolConfig);
							previousMasterAddress = masterAddress;
						}
						sleep(2000);
					}
				} catch (JedisConnectionException e) {

					if (sentinelJedis != null) {
						sentinelPool.returnBrokenResource(sentinelJedis);
					}

					if (running.get()) {
						logger.error("Lost connection with Sentinel " + sentinelPool.getAddress()
								+ ", sleep 1 seconds and try to connect other one. ");
						sleep(1000);
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					sleep(1000);
				}
			}
		}

		public HostAndPort getCurrentMasterAddress() {
			return previousMasterAddress;
		}

		/**
		 * Pickup the first available sentinel, if all sentinel down, return null.
		 */
		private JedisPool pickupSentinel() {
			for (JedisPool pool : sentinelPools) {
				if (JedisUtils.ping(pool)) {
					return pool;
				}
			}

			return null;
		}

		private boolean isAddressChange(HostAndPort currentMasterAddress) {
			if (previousMasterAddress == null) {
				return true;
			}

			return !previousMasterAddress.equals(currentMasterAddress);
		}

		/**
		 * Query master address from sentinel.
		 */
		private HostAndPort queryMasterAddress() {
			JedisTemplate sentinelTemplate = new JedisTemplate(sentinelPool);
			List<String> address = sentinelTemplate.execute(new JedisAction<List<String>>() {
				@Override
				public List<String> action(Jedis jedis) {
					return jedis.sentinelGetMasterAddrByName(masterName);
				}
			});

			if ((address == null) || address.isEmpty()) {
				throw new IllegalArgumentException("Master name " + masterName + " is not in sentinel.conf");
			}

			return new HostAndPort(address.get(0), Integer.valueOf(address.get(1)));
		}

		private void sleep(int millseconds) {
			try {
				Thread.sleep(millseconds);
			} catch (InterruptedException e1) {
				Thread.currentThread().interrupt();
			}
		}

		/**
		 * Subscriber for the master switch message from sentinel and init the new pool.
		 */
		private class MasterSwitchSubscriber extends JedisPubSub {
			@Override
			public void onMessage(String channel, String message) {
				// message example: +switch-master: mymaster 127.0.0.1 6379 127.0.0.1 6380
				// +redirect-to-master default 127.0.0.1 6380 127.0.0.1 6381 (if slave-master fail-over quick enough)
				logger.info("Sentinel " + sentinelPool.getAddress() + " published: " + message);
				String[] switchMasterMsg = message.split(" ");
				// if the master name equals my master name, destroy the old pool and init a new pool
				if (masterName.equals(switchMasterMsg[0])) {

					HostAndPort masterAddress = new HostAndPort(switchMasterMsg[3],
							Integer.parseInt(switchMasterMsg[4]));
					logger.info("Switch master to " + masterAddress);
					destroyInternelPool();
					initInternalPool(masterAddress, masterConnectionInfo, masterPoolConfig);
					previousMasterAddress = masterAddress;
				}
			}

			@Override
			public void onPMessage(String pattern, String channel, String message) {
			}

			@Override
			public void onSubscribe(String channel, int subscribedChannels) {
			}

			@Override
			public void onUnsubscribe(String channel, int subscribedChannels) {
			}

			@Override
			public void onPUnsubscribe(String pattern, int subscribedChannels) {
			}

			@Override
			public void onPSubscribe(String pattern, int subscribedChannels) {
			}
		}
	}
}