package org.springside.examples.miniweb.functional;

import static org.junit.Assert.*;

import java.net.URL;
import java.sql.Driver;

import org.eclipse.jetty.server.Server;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springside.modules.test.data.DataFixtures;
import org.springside.modules.test.functional.JettyFactory;
import org.springside.modules.test.functional.Selenium2;
import org.springside.modules.test.functional.WebDriverFactory;
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

		baseUrl = propertiesLoader.getProperty("baseUrl", MiniWebServer.BASE_URL);

		Boolean isEmbedded = new URL(baseUrl).getHost().equals("localhost");

		if (isEmbedded) {
			startJettyOnce();
		}

		buildDataSourceOnce();
		reloadSampleData();

		createSeleniumOnce();
		loginAsAdminIfNecessary();
	}

	/**
	 * 启动Jetty服务器, 仅启动一次.
	 */
	protected static void startJettyOnce() throws Exception {
		if (jettyServer == null) {
			//设定Spring的profile
			System.setProperty("spring.profiles.active", "functional");

			jettyServer = JettyFactory.createServerInSource(new URL(baseUrl).getPort(), MiniWebServer.CONTEXT);
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

	/**
	 * 创建Selenium.
	 */
	protected static void createSeleniumOnce() throws Exception {

		if (s == null) {
			//根据配置创建Selenium driver.
			String driverName = propertiesLoader.getProperty("selenium.driver");

			WebDriver driver = WebDriverFactory.createDriver(driverName);

			s = new Selenium2(driver, baseUrl);
			s.setStopAtShutdown();
		}
	}

	/**
	 * 登录管理员, 如果用户还没有登录.
	 */
	protected static void loginAsAdminIfNecessary() {
		s.open("/account/user");

		if ("Mini-Web示例:登录页".equals(s.getTitle())) {
			s.type(By.name("username"), "admin");
			s.type(By.name("password"), "admin");
			s.check(By.name("rememberMe"));
			s.click(By.id("submit"));
			assertEquals("Mini-Web示例:帐号管理", s.getTitle());
		}
	}

	/**
	 * 登录特定用户.
	 */
	protected static void login(String user, String password) {
		s.open("/logout");
		s.type(By.name("username"), user);
		s.type(By.name("password"), password);
		s.click(By.id("submit"));
	}

}