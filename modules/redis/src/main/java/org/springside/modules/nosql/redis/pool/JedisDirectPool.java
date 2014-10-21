/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.nosql.redis.pool;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Pool which connect to redis instance directly.
 */
public class JedisDirectPool extends JedisPool {

	public JedisDirectPool(HostAndPort address, JedisPoolConfig config) {
		initInternalPool(address, new ConnectionInfo(), config);
	}

	public JedisDirectPool(HostAndPort address, ConnectionInfo connectionInfo, JedisPoolConfig config) {
		initInternalPool(address, connectionInfo, config);
	}
}
