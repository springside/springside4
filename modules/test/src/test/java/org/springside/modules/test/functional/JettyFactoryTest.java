package org.springside.modules.test.functional;

import static org.junit.Assert.*;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Test;
import org.springside.modules.test.jetty.JettyFactory;

public class JettyFactoryTest {

	@Test
	public void createServer() {
		Server server = JettyFactory.createServerInSource(1978, "/test");

		assertEquals(1978, server.getConnectors()[0].getPort());
		assertEquals("/test", ((WebAppContext) server.getHandler()).getContextPath());
		assertEquals("src/main/webapp", ((WebAppContext) server.getHandler()).getWar());
	}
}
