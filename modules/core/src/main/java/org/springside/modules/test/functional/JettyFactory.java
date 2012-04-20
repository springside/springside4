/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.springside.modules.test.functional;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * 创建Jetty Server的工厂类.
 * 
 * @author calvin
 */
public class JettyFactory {

	private static final String DEFAULT_WEBAPP_PATH = "src/main/webapp";

	private JettyFactory() {
	}

	/**
	 * 创建用于开发运行调试的Jetty Server, 以src/main/webapp为Web应用目录.
	 */
	public static Server createServer(int port, String contextPath) {
		Server server = new Server(port);
		server.setStopAtShutdown(true);

		WebAppContext webContext = new WebAppContext(DEFAULT_WEBAPP_PATH, contextPath);
		webContext.setClassLoader(Thread.currentThread().getContextClassLoader());
		server.setHandler(webContext);

		return server;
	}
}
