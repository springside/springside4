package org.springside.examples.miniservice.functional;

import java.net.URL;
import java.sql.Driver;

import org.eclipse.jetty.server.Server;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springside.modules.test.data.DataFixtures;
import org.springside.modules.test.functional.JettyFactory;
import org.springside.modules.utils.PropertiesLoader;

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

	protected static String baseUrl;

	protected static Server jettyServer;

	protected static SimpleDriverDataSource dataSource;

	protected static PropertiesLoader propertiesLoader = new PropertiesLoader(
			"classpath:/application.functional.properties", "classpath:/application.functional-local.properties");

	private static Logger logger = LoggerFactory.getLogger(BaseFunctionalTestCase.class);

	@BeforeClass
	public static void beforeClass() throws Exception {
		baseUrl = propertiesLoader.getProperty("baseUrl", MiniServiceServer.BASE_URL);

		Boolean isEmbedded = new URL(baseUrl).getHost().equals("localhost");

		if (isEmbedded) {
			startJettyOnce();
		}

		buildDataSourceOnce();
		reloadSampleData();
	}

	/**
	 * 启动Jetty服务器, 在整个功能测试期间仅启动一次.
	 */
	protected static void startJettyOnce() throws Exception {
		if (jettyServer == null) {
			//设定Spring的profile
			System.setProperty("spring.profiles.active", "functional");

			jettyServer = JettyFactory.createServer(new URL(baseUrl).getPort(), MiniServiceServer.CONTEXT);
			jettyServer.start();

			logger.info("Jetty Server started");
		}
	}

	/**
	 * 构造数据源，仅构造一次.
	 */
	protected static void buildDataSourceOnce() throws ClassNotFoundException {
		if (dataSource == null) {
			dataSource = new SimpleDriverDataSource();
			dataSource.setDriverClass((Class<? extends Driver>) Class.forName(propertiesLoader
					.getProperty("jdbc.driver")));
			dataSource.setUrl(propertiesLoader.getProperty("jdbc.url"));
			dataSource.setUsername(propertiesLoader.getProperty("jdbc.username"));
			dataSource.setPassword(propertiesLoader.getProperty("jdbc.password"));
		}
	}

	/**
	 * 载入测试数据.
	 */
	protected static void reloadSampleData() throws Exception {
		DataFixtures.reloadData(dataSource, "/data/sample-data.xml");
	}
}
