package org.springside.examples.quickstart.functional;

import java.net.URL;
import java.sql.Driver;

import org.eclipse.jetty.server.Server;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springside.modules.test.data.DataFixtures;
import org.springside.modules.test.jetty.JettyFactory;
import org.springside.modules.test.selenium.Selenium2;
import org.springside.modules.utils.PropertiesLoader;

/**
 * 功能测试基类.
 * 
 * 在整个测试期间启动一次Jetty Server和 Selenium，在JVM退出时关闭两者。 
 * 在每个TestCase Class执行前重新载入默认数据.
 *  
 * @author calvin
 */
public class BaseFunctionalTestCase {

	protected static String baseUrl;

	protected static Server jettyServer;

	protected static SimpleDriverDataSource dataSource;

	protected static Selenium2 s;

	protected static PropertiesLoader propertiesLoader = new PropertiesLoader("classpath:/application.properties",
			"classpath:/application.functional.properties", "classpath:/application.functional-local.properties");

	private static Logger logger = LoggerFactory.getLogger(BaseFunctionalTestCase.class);

	@BeforeClass
	public static void beforeClass() throws Exception {

		baseUrl = propertiesLoader.getProperty("baseUrl", QuickStartServer.BASE_URL);

		//如果是目标地址是localhost，则启动嵌入式jetty。如果指向远程地址，则不需要启动Jetty.
		Boolean isEmbedded = new URL(baseUrl).getHost().equals("localhost");

		if (isEmbedded) {
			startJettyOnce();
		}

		buildDataSourceOnce();
		reloadSampleData();
	}

	/**
	 * 启动Jetty服务器, 仅启动一次.
	 */
	protected static void startJettyOnce() throws Exception {
		if (jettyServer == null) {
			//设定Spring的profile
			System.setProperty("spring.profiles.active", "functional");

			jettyServer = JettyFactory.createServerInSource(new URL(baseUrl).getPort(), QuickStartServer.CONTEXT);
			JettyFactory.setTldJarNames(jettyServer, QuickStartServer.TLD_JAR_NAMES);
			jettyServer.start();

			logger.info("Jetty Server started");
		}
	}

	/**
	 * 构造数据源，仅构造一次.
	 * 连接参数从配置文件中读取，可指向本地的开发环境，也可以指向远程的测试服务器。
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
		DataFixtures.executeScript(dataSource, "classpath:data/cleanup-data.sql", "classpath:data/import-data.sql");
	}
}