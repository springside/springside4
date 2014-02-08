package org.springside.modules.nosql.redis.pool;

import redis.clients.jedis.Protocol;

/**
 * All information for redis connection.
 */
public class ConnectionInfo {

	public static final int DEFAULT_PORT = Protocol.DEFAULT_PORT;
	public static final int DEFAULT_SENTINEL_PORT = 26379;
	public static final String DEFAULT_PASSWORD = null;
	public static final int DEFAULT_TIMEOUT = Protocol.DEFAULT_TIMEOUT;
	public static final int DEFAULT_DATABASE = Protocol.DEFAULT_DATABASE;

	private String host;
	private int port;
	private int timeout;
	private String password;
	private int database;
	private String clientName;

	public ConnectionInfo(String host) {
		this(host, DEFAULT_PORT, DEFAULT_TIMEOUT, DEFAULT_PASSWORD, DEFAULT_DATABASE, null);
	}

	public ConnectionInfo(String host, int port) {
		this(host, port, DEFAULT_TIMEOUT, DEFAULT_PASSWORD, DEFAULT_DATABASE, null);
	}

	public ConnectionInfo(String host, int port, int timeout) {
		this(host, port, timeout, DEFAULT_PASSWORD, DEFAULT_DATABASE, null);
	}

	public ConnectionInfo(String host, int port, int timeout, String password, int database, String clientName) {
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		this.password = password;
		this.database = database;
		this.clientName = clientName;
	}

	public String getHostAndPort() {
		return new StringBuilder().append(host).append(":").append(port).toString();
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getDatabase() {
		return database;
	}

	public void setDatabase(int database) {
		this.database = database;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	@Override
	public String toString() {
		return "ConnectionInfo [host=" + host + ", port=" + port + ", timeout=" + timeout + ", password=" + password
				+ ", database=" + database + ", clientName=" + clientName + "]";
	}
}
