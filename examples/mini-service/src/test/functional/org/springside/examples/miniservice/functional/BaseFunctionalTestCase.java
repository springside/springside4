package org.springside.examples.miniservice.functional;

import javax.sql.DataSource;

import org.eclipse.jetty.server.Server;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.test.data.H2Fixtures;
import org.springside.modules.test.functional.JettyFactory;
import org.springside.modules.test.spring.SpringContextHolder;

/**
 * 功能测试基类.
 * 
 * 在整个测试期间启动一次Jetty Server.
 * 在每个TestCase执行前重新载入默认数据
 * 
 * @author calvin
 */
@Ignore
public class BaseFunctionalTestCase {

	protected static final String BASE_URL = Start.BASE_URL;

	protected static Server jettyServer;

	protected static DataSource dataSource;

	private static Logger logger = LoggerFactory.getLogger(BaseFunctionalTestCase.class);

	@BeforeClass
	public static void startAll() throws Exception {
		startJetty();
		reloadSampleData();
	}

	/**
	 * 启动Jetty服务器, 在整个功能测试期间仅启动一次.
	 */
	protected static void startJetty() throws Exception {
		if (jettyServer == null) {
			jettyServer = JettyFactory.buildTestServer(Start.TEST_PORT, Start.CONTEXT);
			jettyServer.start();

			logger.info("Jetty Server started");

			dataSource = SpringContextHolder.getBean("dataSource");
		}
	}

	/**
	 * 载入测试数据.
	 */
	protected static void reloadSampleData() throws Exception {
		H2Fixtures.reloadAllTable(dataSource, "/data/sample-data.xml");
	}
}
