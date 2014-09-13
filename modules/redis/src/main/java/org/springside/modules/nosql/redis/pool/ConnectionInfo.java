/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.nosql.redis.pool;

import redis.clients.jedis.Protocol;

/**
 * All information for redis connection.
 */
public class ConnectionInfo {

	public static final String DEFAULT_PASSWORD = null;

	private int database = Protocol.DEFAULT_DATABASE;
	private String password = DEFAULT_PASSWORD;
	private int timeout = Protocol.DEFAULT_TIMEOUT;

	public ConnectionInfo() {
	}

	public ConnectionInfo(int database, String password, int timeout) {
		this.timeout = timeout;
		this.password = password;
		this.database = database;
	}

	public int getDatabase() {
		return database;
	}

	public void setDatabase(int database) {
		this.database = database;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public String toString() {
		return "ConnectionInfo [database=" + database + ", password=" + password + ", timeout=" + timeout + "]";
	}
}
