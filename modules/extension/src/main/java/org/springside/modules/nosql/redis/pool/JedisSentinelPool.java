package org.springside.modules.nosql.redis.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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

/**
 * Pool which to get redis master address get from sentinel instances.
 */
public class JedisSentinelPool extends JedisPool {

	public static final String DEFAULT_MASTER_NAME = "main";
	public static final String NO_ADDRESS_YET = "I dont know because no sentinel up";

	private static Logger logger = LoggerFactory.getLogger(JedisSentinelPool.class);

	private List<JedisPool> sentinelPools = new ArrayList<JedisPool>();
	private MasterSwitchListener masterSwitchListener;

	private JedisPoolConfig redisPoolConfig;
	private String masterName;
	private ConnectionInfo redisAddtionalInfo;

	public JedisSentinelPool(ConnectionInfo sentinelInfo, ConnectionInfo redisAddtionalInfo, String masterName,
			JedisPoolConfig redisPoolConfig) {
		this(new ConnectionInfo[] { sentinelInfo }, redisAddtionalInfo, masterName, redisPoolConfig);
	}

	/**
	 * Creates a new instance of <code>JedisSentinelPool</code>.
	 * 
	 * All parameters can be null or empty.
	 * 
	 * @param sentinelInfos Array of connectionInfo to sentinel instances.
	 * @param redisAddtionalInfo The master host and port would dynamic get from sentinel, and the other information
	 *            like password store in this.
	 * @param masterName One sentinel can monitor several redis master-slave pair, use master name to identify them.
	 * @param redisPoolConfig Config of redis pool.
	 * 
	 */
	public JedisSentinelPool(ConnectionInfo[] sentinelInfos, ConnectionInfo redisAddtionalInfo, String masterName,
			JedisPoolConfig redisPoolConfig) {
		// sentinelInfos
		assertArgument(((sentinelInfos == null) || (sentinelInfos.length == 0)), "seintinelInfos is not set");

		for (ConnectionInfo sentinelInfo : sentinelInfos) {
			JedisPool sentinelPool = new JedisDirectPool(sentinelInfo, new JedisPoolConfig());
			sentinelPools.add(sentinelPool);
		}

		// redisAddtionalInfo
		assertArgument(redisAddtionalInfo == null, "redisAddtionalInfo is not set");
		this.redisAddtionalInfo = redisAddtionalInfo;

		// masterName
		assertArgument(((masterName == null) || masterName.isEmpty()), "masterName is not set");
		this.masterName = masterName;

		// poolConfig
		assertArgument(redisPoolConfig == null, "redisPoolConfig is not set");
		this.redisPoolConfig = redisPoolConfig;

		// init pool and listener
		startMasterSwitchListener();
	}

	/**
	 * Get the master address which is read from the sentinel.
	 * 
	 * @return the ConnectionInfo of Master Redis server.
	 */
	public ConnectionInfo getMasterAddress() {
		JedisPool anySentinelPool = pickupSentinel();
		if (anySentinelPool == null) {
			return new ConnectionInfo(NO_ADDRESS_YET);
		}

		return queryMasterAddress(anySentinelPool);
	}

	/**
	 * Pickup the first available sentinel, if all sentinel down, return null.
	 */
	private JedisPool pickupSentinel() {
		for (JedisPool sentinelPool : sentinelPools) {
			if (ping(sentinelPool)) {
				return sentinelPool;
			}
		}

		return null;
	}

	/**
	 * Query master address from sentinel.
	 */
	private ConnectionInfo queryMasterAddress(JedisPool sentinelPool) {
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

		return buildRedisInfo(address.get(0), address.get(1));
	}

	/**
	 * Combine the host & port with the redisAddtionalInfo which store the other properties.
	 */
	private ConnectionInfo buildRedisInfo(String host, String port) {
		return new ConnectionInfo(host, Integer.valueOf(port), redisAddtionalInfo.getTimeout(),
				redisAddtionalInfo.getPassword(), redisAddtionalInfo.getDatabase());
	}

	/**
	 * Start MasterSwitchListener thread to listen the master switch message.
	 */
	private void startMasterSwitchListener() {
		masterSwitchListener = new MasterSwitchListener();
		masterSwitchListener.start();
	}

	/**
	 * Assert the argurment, throw IllegalArgumentException if the expression is true.
	 */
	private void assertArgument(boolean expression, String message) {
		if (expression) {
			throw new IllegalArgumentException(message);
		}
	}

	@Override
	public void destroy() {
		// shutdown the listener thread
		masterSwitchListener.stopListening();

		// destroy sentinel pools
		for (JedisPool sentinel : sentinelPools) {
			sentinel.destroy();
		}

		// destroy redis pool
		destroyRedisPool();

		// wait for the masterSwitchListener thread finish
		try {
			logger.info("Waiting for MasterSwitchListener thread finish");
			masterSwitchListener.join();
			logger.info("MasterSwitchListener thread finished");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Ping the jedis instance, return true is the result is PONG.
	 */
	public static boolean ping(JedisPool pool) {
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

	// for test
	MasterSwitchListener getMasterSwitchListener() {
		return masterSwitchListener;
	}

	/**
	 * Listener thread to listen master switch message from sentinel.
	 */
	class MasterSwitchListener extends Thread {
		public static final String THREAD_NAME = "MasterSwitchListener";

		private JedisPubSub subscriber;
		private JedisPool sentinelPool;
		private Jedis sentinelJedis;
		private AtomicBoolean running = new AtomicBoolean(true);
		private ConnectionInfo preConnectionInfo;

		public MasterSwitchListener() {
			super(THREAD_NAME);
		}

		// stop the blocking subscription and interrupt the thread
		public void stopListening() {
			// interrupt the thread
			running = new AtomicBoolean(false);
			this.interrupt();

			// stop the blocking subscription
			try {
				if (subscriber != null) {
					subscriber.unsubscribe();
				}
			} finally {
				if (sentinelJedis != null) {
					JedisUtils.closeJedis(sentinelJedis);
				}
			}
		}

		@Override
		public void run() {
			while (running.get()) {
				try {
					sentinelPool = pickupSentinel();

					if (sentinelPool != null) {

						ConnectionInfo currentConnectionInfo = queryMasterAddress(sentinelPool);

						if ((internalPool != null) && isAddressChange(currentConnectionInfo)) {
							logger.info("The internalPool {} had changed, destroy it now.",
									preConnectionInfo.getHostAndPort());
							destroyRedisPool();
						}

						if ((internalPool == null) || isAddressChange(currentConnectionInfo)) {
							logger.info("The internalPool is not init or the address had changed, init it now.");
							initRedisPool(currentConnectionInfo, redisPoolConfig);
						}

						preConnectionInfo = currentConnectionInfo;

						sentinelJedis = sentinelPool.getResource();
						subscriber = new MasterSwitchSubscriber();
						sentinelJedis.subscribe(subscriber, "+switch-master", "+redirect-to-master");
					} else {
						logger.info("All sentinels down, sleep 2 seconds and try to connect again.");
						// TODO:calvin when the system startup but the sentinels not yet, init a urgly address to
						// prevent null point exception. change the logic later.
						if (internalPool == null) {
							ConnectionInfo currentConnectionInfo = new ConnectionInfo(NO_ADDRESS_YET);
							initRedisPool(currentConnectionInfo, redisPoolConfig);
							preConnectionInfo = currentConnectionInfo;
						}
						sleep(2000);
					}
				} catch (JedisConnectionException e) {

					if (sentinelJedis != null) {
						sentinelPool.returnBrokenResource(sentinelJedis);
					}

					if (running.get()) {
						logger.error("Lost connection with Sentinel "
								+ sentinelPool.getConnectionInfo().getHostAndPort()
								+ ", sleep 1 seconds and try to connect other one. ");
						sleep(1000);
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					sleep(1000);
				}
			}
		}

		private boolean isAddressChange(ConnectionInfo currentConnectionInfo) {
			if (preConnectionInfo == null) {
				return true;
			}

			return (preConnectionInfo.getHostAndPort().equals(currentConnectionInfo.getHostAndPort()));
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
				logger.info("Sentinel " + sentinelPool.getConnectionInfo().getHostAndPort() + " published: " + message);
				String[] switchMasterMsg = message.split(" ");
				// if the master name equals my master name, destroy the old pool and init a new pool
				if (masterName.equals(switchMasterMsg[0])) {
					destroyRedisPool();

					ConnectionInfo redisInfo = buildRedisInfo(switchMasterMsg[3], switchMasterMsg[4]);
					logger.info("Switch master to " + redisInfo.getHostAndPort());

					initRedisPool(redisInfo, redisPoolConfig);

					preConnectionInfo = redisInfo;
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
