package org.springside.modules.test.functional;

import static org.junit.Assert.*;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Test;
import org.springside.modules.test.functional.JettyFactory;

public class JettyFactoryTest {

	@Test
	public void buildNormalServer() {
		Server server = JettyFactory.buildNormalServer(1978, "core");

		assertEquals(1978, server.getConnectors()[0].getPort());
		assertEquals("core", ((WebAppContext) server.getHandler()).getContextPath());
		assertEquals("src/main/webapp", ((WebAppContext) server.getHandler()).getWar());
	}

	@Test
	public void buildTestServer() {
		Server server = JettyFactory.buildTestServer(1978, "core");

		assertEquals(1978, server.getConnectors()[0].getPort());
		assertEquals("core", ((WebAppContext) server.getHandler()).getContextPath());
		assertEquals("src/main/webapp", ((WebAppContext) server.getHandler()).getWar());
		assertEquals("src/test/resources/web.xml", ((WebAppContext) server.getHandler()).getDescriptor());
	}
}
