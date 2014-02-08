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
 * Pool which to get redis master address get from sentinel instances.
 */
public class JedisSentinelPool extends Pool<Jedis> {

	public static final String UNAVAILABLE_ADDRESS = "I dont know because no sentinel up";

	private static Logger logger = LoggerFactory.getLogger(JedisSentinelPool.class);

	private List<JedisDirectPool> sentinelPools = new ArrayList<JedisDirectPool>();
	private MasterSwitchListener masterSwitchListener;

	private String masterName;

	private JedisPoolConfig redisPoolConfig;
	private ConnectionInfo redisAddtionalInfo;

	/**
	 * see {@link #JedisSentinelPool(ConnectionInfo[], ConnectionInfo, String, JedisPoolConfig)}
	 */
	public JedisSentinelPool(ConnectionInfo sentinelInfo, ConnectionInfo redisAddtionalInfo, String masterName,
			JedisPoolConfig redisPoolConfig) {
		this(new ConnectionInfo[] { sentinelInfo }, redisAddtionalInfo, masterName, redisPoolConfig);
	}

	/**
	 * Creates a new instance of <code>JedisSentinelPool</code>.
	 * 
	 * @param sentinelInfos Array of connection infomation to sentinel instances.
	 * @param redisAddtionalInfo The master host and port would dynamic get from sentinel, and the other information
	 *            like password, timeout store in it.
	 * @param masterName One sentinel can monitor several redis master-slave pair, use master name to identify it.
	 * @param redisPoolConfig Configuration of redis pool.
	 */
	public JedisSentinelPool(ConnectionInfo[] sentinelInfos, ConnectionInfo redisAddtionalInfo, String masterName,
			JedisPoolConfig redisPoolConfig) {
		// check and assign parameter, all parameter can't not be null or empty
		assertArgument(((sentinelInfos == null) || (sentinelInfos.length == 0)), "seintinelInfos is not set");
		for (ConnectionInfo sentinelInfo : sentinelInfos) {
			JedisDirectPool sentinelPool = new JedisDirectPool(sentinelInfo, new JedisPoolConfig());
			sentinelPools.add(sentinelPool);
		}

		assertArgument(redisAddtionalInfo == null, "redisAddtionalInfo is not set");
		this.redisAddtionalInfo = redisAddtionalInfo;

		assertArgument(((masterName == null) || masterName.isEmpty()), "masterName is not set");
		this.masterName = masterName;

		assertArgument(redisPoolConfig == null, "redisPoolConfig is not set");
		this.redisPoolConfig = redisPoolConfig;

		// start switch listener, the listener will init the pool.
		startMasterSwitchListener();
	}

	/**
	 * Start MasterSwitchListener thread to listen the master switch message.
	 */
	private void startMasterSwitchListener() {
		masterSwitchListener = new MasterSwitchListener();
		masterSwitchListener.start();
	}

	/**
	 * Pickup the first available sentinel, if all sentinel down, return null.
	 */
	private JedisDirectPool pickupSentinel() {
		for (JedisDirectPool sentinelPool : sentinelPools) {
			if (ping(sentinelPool)) {
				return sentinelPool;
			}
		}

		return null;
	}

	/**
	 * Query master address from sentinel.
	 */
	private ConnectionInfo queryMasterAddress(JedisDirectPool sentinelPool) {
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

		return buildRedisConnectionInfo(address.get(0), address.get(1));
	}

	/**
	 * Combine the host & port with the redisAddtionalInfo which store the other properties.
	 */
	private ConnectionInfo buildRedisConnectionInfo(String host, String port) {
		return new ConnectionInfo(host, Integer.valueOf(port), redisAddtionalInfo.getTimeout(),
				redisAddtionalInfo.getPassword(), redisAddtionalInfo.getDatabase());
	}

	@Override
	public void destroy() {
		masterSwitchListener.stopListening();

		for (JedisDirectPool sentinel : sentinelPools) {
			sentinel.destroy();
		}

		destroyInternalRedisPool();

		// wait for the masterSwitchListener thread finish
		try {
			logger.info("Waiting for MasterSwitchListener thread finish");
			masterSwitchListener.join();
			logger.info("MasterSwitchListener thread finished");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private void initInternalRedisPool(ConnectionInfo currentConnectionInfo) {
		JedisFactory factory = new JedisFactory(currentConnectionInfo.getHost(), currentConnectionInfo.getPort(),
				currentConnectionInfo.getTimeout(), currentConnectionInfo.getPassword(),
				currentConnectionInfo.getDatabase());

		internalPool = new GenericObjectPool(factory, redisPoolConfig);
	}

	private void destroyInternalRedisPool() {
		super.destroy();
	}

	/**
	 * Ping the jedis instance, return true is the result is PONG.
	 */
	public static boolean ping(JedisDirectPool pool) {
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
	 * Assert the argurment, throw IllegalArgumentException if the expression is true.
	 */
	private static void assertArgument(boolean expression, String message) throws IllegalArgumentException {
		if (expression) {
			throw new IllegalArgumentException(message);
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
		public static final String THREAD_NAME = "RedisMasterSwitchListener-";

		private JedisPubSub subscriber;
		private JedisDirectPool sentinelPool;
		private Jedis sentinelJedis;

		private AtomicBoolean running = new AtomicBoolean(true);
		private ConnectionInfo previousConnectionInfo;

		public MasterSwitchListener() {
			super(THREAD_NAME + masterName);
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
									previousConnectionInfo.getHostAndPort());
							destroyInternalRedisPool();
						}

						if ((internalPool == null) || isAddressChange(currentConnectionInfo)) {
							logger.info("The internalPool is not init or the address had changed, init it now.");
							initInternalRedisPool(currentConnectionInfo);
						}

						previousConnectionInfo = currentConnectionInfo;

						sentinelJedis = sentinelPool.getResource();
						subscriber = new MasterSwitchSubscriber();
						sentinelJedis.subscribe(subscriber, "+switch-master", "+redirect-to-master");
					} else {
						logger.info("All sentinels down, sleep 1 seconds and try to connect again.");
						// when the system startup but the sentinels not yet, init a urgly address to
						// prevent null point exception.
						if (internalPool == null) {
							ConnectionInfo currentConnectionInfo = new ConnectionInfo(UNAVAILABLE_ADDRESS);
							initInternalRedisPool(currentConnectionInfo);
							previousConnectionInfo = currentConnectionInfo;
						}
						sleep(1000);
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

		/**
		 * interrupt the thread and stop the blocking subscription.
		 */
		public void stopListening() {
			running = new AtomicBoolean(false);
			this.interrupt();

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

		private boolean isAddressChange(ConnectionInfo currentConnectionInfo) {
			if (currentConnectionInfo == null) {
				return false;
			}

			if (previousConnectionInfo == null) {
				return true;
			}

			return (previousConnectionInfo.getHostAndPort().equals(currentConnectionInfo.getHostAndPort()));
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
				// +redirect-to-master default 127.0.0.1 6380 127.0.0.1 6381 (if slave-master fail-over quick enough)
				logger.info("Sentinel " + sentinelPool.getConnectionInfo().getHostAndPort() + " published: " + message);
				String[] switchMasterMsg = message.split(" ");
				// if the master name equals my master name, destroy the old pool and init a new pool
				if (masterName.equals(switchMasterMsg[0])) {
					destroyInternalRedisPool();

					ConnectionInfo redisInfo = buildRedisConnectionInfo(switchMasterMsg[3], switchMasterMsg[4]);
					logger.info("Switch master to " + redisInfo.getHostAndPort());

					initInternalRedisPool(redisInfo);

					previousConnectionInfo = redisInfo;
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
