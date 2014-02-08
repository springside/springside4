package org.springside.modules.nosql.redis.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.nosql.redis.JedisTemplate;
import org.springside.modules.nosql.redis.JedisTemplate.JedisAction;
import org.springside.modules.nosql.redis.JedisUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.Pool;

/**
 * Pool which to get redis master address from sentinel instances.
 */
public class JedisSentinelPool extends Pool<Jedis> {

	public static final String UNAVAILABLE_ADDRESS = "I dont know because no sentinel up";

	private static Logger logger = LoggerFactory.getLogger(JedisSentinelPool.class);

	private String masterName;

	private List<JedisPool> sentinelPools = new ArrayList<JedisPool>();
	private MasterSwitchListener masterSwitchListener;

	private JedisPoolConfig masterPoolConfig;
	private ConnectionInfo masterAddtionalInfo;

	/**
	 * see {@link #JedisSentinelPool(ConnectionInfo[], String, ConnectionInfo, JedisPoolConfig)}
	 */
	public JedisSentinelPool(ConnectionInfo sentinelInfo, String masterName, ConnectionInfo masterAddtionalInfo,
			JedisPoolConfig masterPoolConfig) {
		this(new ConnectionInfo[] { sentinelInfo }, masterName, masterAddtionalInfo, masterPoolConfig);
	}

	/**
	 * Creates a new instance of <code>JedisSentinelPool</code>.
	 * 
	 * @param sentinelInfos Array of connection information to sentinel instances.
	 * @param masterName One sentinel can monitor several redis master-slave pair, use master name to identify it.
	 * @param masterAddtionalInfo The master host and port would dynamic get from sentinel, and the other information
	 *            like password, timeout store in it.
	 * @param masterPoolConfig Configuration of redis pool.
	 */
	public JedisSentinelPool(ConnectionInfo[] sentinelInfos, String masterName, ConnectionInfo masterAddtionalInfo,
			JedisPoolConfig masterPoolConfig) {
		// check and assign parameter, all parameter can't not be null or empty
		assertArgument(((sentinelInfos != null) && (sentinelInfos.length != 0)), "seintinelInfos is not set");
		for (ConnectionInfo sentinelInfo : sentinelInfos) {
			JedisPool sentinelPool = new JedisPool(sentinelInfo, new JedisPoolConfig());
			sentinelPools.add(sentinelPool);
		}

		assertArgument(masterAddtionalInfo != null, "masterAddtionalInfo is not set");
		this.masterAddtionalInfo = masterAddtionalInfo;

		assertArgument(((masterName != null) && !masterName.isEmpty()), "masterName is not set");
		this.masterName = masterName;

		assertArgument(masterPoolConfig != null, "masterPoolConfig is not set");
		this.masterPoolConfig = masterPoolConfig;

		// Start MasterSwitchListener thread to listen the master switch message.
		masterSwitchListener = new MasterSwitchListener();
		masterSwitchListener.start();
	}

	@Override
	public void destroy() {
		masterSwitchListener.shutdown();

		for (JedisPool sentinelPool : sentinelPools) {
			sentinelPool.destroy();
		}

		destroyInternalPool();

		try {
			logger.info("Waiting for MasterSwitchListener thread finish");
			masterSwitchListener.join();
			logger.info("MasterSwitchListener thread finished");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void returnResource(final Jedis resource) {
		resource.resetState();
		returnResourceObject(resource);
	}

	private void initInternalPool(ConnectionInfo masterConnectionInfo) {
		JedisFactory factory = new JedisFactory(masterConnectionInfo.getHost(), masterConnectionInfo.getPort(),
				masterConnectionInfo.getTimeout(), masterConnectionInfo.getPassword(),
				masterConnectionInfo.getDatabase(), masterConnectionInfo.getClientName());

		internalPool = new GenericObjectPool(factory, masterPoolConfig);
	}

	private void destroyInternalPool() {
		super.destroy();
	}

	/**
	 * Assert the argument, throw IllegalArgumentException if the expression is false.
	 */
	private static void assertArgument(boolean expression, String message) throws IllegalArgumentException {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}

	// method for test
	MasterSwitchListener getMasterSwitchListener() {
		return masterSwitchListener;
	}

	/**
	 * Listener thread to listen master switch message from sentinel.
	 */
	class MasterSwitchListener extends Thread {
		public static final String THREAD_NAME_PREFIX = "RedisMasterSwitchListener-";
		public static final int RETRY_WAIT_TIME_MILLS = 1000;

		private JedisPool currentSentinelPool;
		private Jedis subscriberJedis;
		private JedisPubSub subscriber;

		private AtomicBoolean running = new AtomicBoolean(true);
		private ConnectionInfo previousMasterConnectionInfo;

		public MasterSwitchListener() {
			super(THREAD_NAME_PREFIX + masterName);
		}

		@Override
		public void run() {

			while (running.get()) {
				try {
					currentSentinelPool = selectSentinel();

					if (currentSentinelPool != null) {

						ConnectionInfo masterConnectionInfo = queryMasterAddress(currentSentinelPool);

						if ((internalPool != null) && isMasterAddressChanged(masterConnectionInfo)) {
							logger.info("The internalPool {} had changed, destroy it now.",
									previousMasterConnectionInfo.getHostAndPort());
							destroyInternalPool();
						}

						if ((internalPool == null) || isMasterAddressChanged(masterConnectionInfo)) {
							logger.info("The internalPool {} is not init or the address had changed, init it now.",
									masterConnectionInfo.getHostAndPort());
							initInternalPool(masterConnectionInfo);
						}

						previousMasterConnectionInfo = masterConnectionInfo;

						// blocking listen master switch message until exception happen.
						subscriber = new MasterSwitchSubscriber();
						subscriberJedis = currentSentinelPool.getResource();
						subscriberJedis.subscribe(subscriber, "+switch-master", "+redirect-to-master");
					} else {
						logger.info("All sentinels down, sleep 1000ms and try to select again.");
						// when the system startup but the sentinels not yet, init an ugly address to prevent null point
						// exception.
						if (internalPool == null) {
							ConnectionInfo currentMasterConnectionInfo = new ConnectionInfo(UNAVAILABLE_ADDRESS);
							initInternalPool(currentMasterConnectionInfo);
							previousMasterConnectionInfo = currentMasterConnectionInfo;
						}
						sleep(RETRY_WAIT_TIME_MILLS);
					}
				} catch (JedisConnectionException e) {
					if (subscriberJedis != null) {
						currentSentinelPool.returnBrokenResource(subscriberJedis);
					}

					if (running.get()) {
						logger.error("Lost connection with Sentinel " + currentSentinelPool.getHostAndPort()
								+ ", sleep 1000ms and try to connect other one.");
						sleep(RETRY_WAIT_TIME_MILLS);
					}
				} catch (Exception e) {
					if (running.get()) {
						logger.error(
								"Some Exception happen, current Sentinel is" + currentSentinelPool.getHostAndPort()
										+ ", sleep 1000ms and try again.", e);
						sleep(RETRY_WAIT_TIME_MILLS);
					}
				}
			}
		}

		/**
		 * Interrupt the thread and stop the blocking subscription.
		 */
		private void shutdown() {
			running.getAndSet(false);
			this.interrupt();

			try {
				if (subscriber != null) {
					subscriber.unsubscribe();
				}
			} finally {
				if (subscriberJedis != null) {
					JedisUtils.closeJedis(subscriberJedis);
				}
			}
		}

		/**
		 * Pickup the first available sentinel, if all sentinel down, return null.
		 */
		private JedisPool selectSentinel() {
			for (JedisPool sentinelPool : sentinelPools) {
				if (ping(sentinelPool)) {
					return sentinelPool;
				}
			}

			return null;
		}

		/**
		 * Ping the jedis instance, return true if the result is PONG.
		 */
		private boolean ping(Pool<Jedis> pool) {
			JedisTemplate template = new JedisTemplate(pool);
			try {
				String result = template.execute(new JedisAction<String>() {
					@Override
					public String action(Jedis jedis) {
						return jedis.ping();
					}
				});
				return (result != null) && result.equals("PONG");
			} catch (JedisException e) {
				return false;
			}
		}

		/**
		 * Query master address from sentinel.
		 */
		private ConnectionInfo queryMasterAddress(Pool<Jedis> sentinelPool) {
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

			return buildMasterConnectionInfo(address.get(0), address.get(1));
		}

		/**
		 * Combine the host & port with the masterAddtionalInfo which store the other properties.
		 */
		private ConnectionInfo buildMasterConnectionInfo(String host, String port) {
			return new ConnectionInfo(host, Integer.valueOf(port), masterAddtionalInfo.getTimeout(),
					masterAddtionalInfo.getPassword(), masterAddtionalInfo.getDatabase(),
					masterAddtionalInfo.getClientName());
		}

		private boolean isMasterAddressChanged(ConnectionInfo currentMasterConnectionInfo) {
			if (previousMasterConnectionInfo == null) {
				return true;
			}

			return (previousMasterConnectionInfo.getHostAndPort().equals(currentMasterConnectionInfo.getHostAndPort()));
		}

		private void sleep(int millseconds) {
			try {
				Thread.sleep(millseconds);
			} catch (InterruptedException e) {
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
				// +redirect-to-master mymaster 127.0.0.1 6380 127.0.0.1 6381 (if slave-master fail-over quick enough)
				logger.info("Sentinel " + currentSentinelPool.getHostAndPort() + " published: " + message);
				String[] switchMasterMsg = message.split(" ");
				// if the switeched master name equals my master name, destroy the old pool and init a new pool
				if (masterName.equals(switchMasterMsg[0])) {
					destroyInternalPool();

					ConnectionInfo masterConnectionInfo = buildMasterConnectionInfo(switchMasterMsg[3],
							switchMasterMsg[4]);
					logger.info("Switch master to " + masterConnectionInfo.getHostAndPort());

					initInternalPool(masterConnectionInfo);

					previousMasterConnectionInfo = masterConnectionInfo;
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
