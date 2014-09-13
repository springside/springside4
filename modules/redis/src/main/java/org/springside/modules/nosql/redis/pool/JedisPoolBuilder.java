/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.nosql.redis.pool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * Build JedisPool smartly。
 * Depends on masterName whether prefix with "direct:", it will build JedisSentinelPool or JedisDirectPool.
 */
public class JedisPoolBuilder {

	public static final String DIRECT_POOL_PREFIX = "direct:";

	private static Logger logger = LoggerFactory.getLogger(JedisPoolBuilder.class);

	private String[] sentinelHosts;
	private int sentinelPort = Protocol.DEFAULT_SENTINEL_PORT;

	private String masterName;
	private String[] shardedMasterNames;

	private int poolSize = -1;

	private int database = Protocol.DEFAULT_DATABASE;
	private String password = ConnectionInfo.DEFAULT_PASSWORD;
	private int timeout = Protocol.DEFAULT_TIMEOUT;

	public JedisPoolBuilder setHosts(String[] hosts) {
		this.sentinelHosts = hosts;
		return this;
	}

	public JedisPoolBuilder setHosts(String hosts) {
		if (hosts != null) {
			this.sentinelHosts = hosts.split(",");
		}
		return this;
	}

	public JedisPoolBuilder setPort(int port) {
		this.sentinelPort = port;
		return this;
	}

	public JedisPoolBuilder setMasterName(String masterName) {
		this.masterName = masterName;
		return this;
	}

	public JedisPoolBuilder setShardedMasterNames(String[] shardedMasterNames) {
		this.shardedMasterNames = shardedMasterNames;
		return this;
	}

	public JedisPoolBuilder setShardedMasterNames(String shardedMasterNames) {
		if (shardedMasterNames != null) {
			this.shardedMasterNames = shardedMasterNames.split(",");
		}
		return this;
	}

	public JedisPoolBuilder setDirectHostAndPort(String host, String port) {
		this.masterName = host + ":" + port;
		return this;
	}

	public JedisPoolBuilder setPoolSize(int poolSize) {
		this.poolSize = poolSize;
		return this;
	}

	public JedisPoolBuilder setDatabase(int database) {
		this.database = database;
		return this;
	}

	public JedisPoolBuilder setPassword(String password) {
		this.password = password;
		return this;
	}

	public JedisPoolBuilder setTimeout(int timeout) {
		this.timeout = timeout;
		return this;
	}

	public JedisPool buildPool() {

		if ((masterName == null) || "".equals(masterName)) {
			throw new IllegalArgumentException("masterName is null or empty");
		}

		if (poolSize < 1) {
			throw new IllegalArgumentException("poolSize is less then one");
		}

		JedisPoolConfig config = JedisPool.createPoolConfig(poolSize);
		ConnectionInfo connectionInfo = new ConnectionInfo(database, password, timeout);

		if (isDirect(masterName)) {
			return buildDirectPool(masterName, connectionInfo, config);
		} else {
			if ((sentinelHosts == null) || (sentinelHosts.length == 0)) {
				throw new IllegalArgumentException("sentinelHosts is null or empty");
			}
			return buildSentinelPool(masterName, connectionInfo, config);
		}
	}

	public List<JedisPool> buildShardedPools() {

		if ((shardedMasterNames == null) || (shardedMasterNames.length == 0) || "".equals(shardedMasterNames[0])) {
			throw new IllegalArgumentException("shardedMasterNames is null or empty");
		}

		if (poolSize < 1) {
			throw new IllegalArgumentException("poolSize is less then one");
		}

		JedisPoolConfig config = JedisPool.createPoolConfig(poolSize);
		ConnectionInfo connectionInfo = new ConnectionInfo(database, password, timeout);

		List<JedisPool> jedisPools = new ArrayList<JedisPool>();

		if (isDirect(shardedMasterNames[0])) {
			for (String theMasterName : shardedMasterNames) {
				jedisPools.add(buildDirectPool(theMasterName, connectionInfo, config));
			}
		} else {

			if ((sentinelHosts == null) || (sentinelHosts.length == 0)) {
				throw new IllegalArgumentException("sentinelHosts is null or empty");
			}

			for (String theMasterName : shardedMasterNames) {
				jedisPools.add(buildSentinelPool(theMasterName, connectionInfo, config));
			}
		}
		return jedisPools;
	}

	private JedisPool buildDirectPool(String directMasterName, ConnectionInfo connectionInfo, JedisPoolConfig config) {
		String hostPortStr = directMasterName.substring(directMasterName.indexOf(":") + 1, directMasterName.length());
		String[] hostPort = hostPortStr.split(":");

		logger.info("Building JedisDirectPool, on redis server " + hostPort[0] + " ,sentinelPort is " + hostPort[1]);

		HostAndPort masterAddress = new HostAndPort(hostPort[0], Integer.parseInt(hostPort[1]));
		return new JedisDirectPool(masterAddress, config);
	}

	private JedisPool buildSentinelPool(String sentinelMasterName, ConnectionInfo connectionInfo, JedisPoolConfig config) {
		logger.info("Building JedisSentinelPool, on sentinel sentinelHosts:" + Arrays.toString(sentinelHosts)
				+ " ,sentinelPort is " + sentinelPort + " ,masterName is " + sentinelMasterName);

		HostAndPort[] sentinelAddress = new HostAndPort[sentinelHosts.length];
		for (int i = 0; i < sentinelHosts.length; i++) {
			sentinelAddress[i] = new HostAndPort(sentinelHosts[i], sentinelPort);
		}

		return new JedisSentinelPool(sentinelAddress, sentinelMasterName, connectionInfo, config);
	}

	private static boolean isDirect(String masterName) {
		return masterName.startsWith(DIRECT_POOL_PREFIX);
	}
}
