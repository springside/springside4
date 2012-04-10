package org.springside.examples.miniweb.functional;

import static org.junit.Assert.*;

import java.net.URL;
import java.sql.Driver;

import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springside.modules.test.data.Fixtures;
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
@Ignore
public class BaseFunctionalTestCase {

	protected static String baseUrl;

	protected static Server jettyServer;

	protected static SimpleDriverDataSource dataSource;

	protected static Selenium2 s;

	protected static PropertiesLoader propertiesLoader = new PropertiesLoader(
			"classpath:/application.functional.properties", "classpath:/application.functional-local.properties");

	private static Logger logger = LoggerFactory.getLogger(BaseFunctionalTestCase.class);

	@BeforeClass
	public static void beforeClass() throws Exception {

		baseUrl = propertiesLoader.getProperty("baseUrl", Start.BASE_URL);

		Boolean isEmbedded = propertiesLoader.getBoolean("embedded", true);

		if (isEmbedded) {
			startJettyOnce();
		}

		buildDataSourceOnce();
		reloadSampleData();

		createSeleniumOnce();
		loginAsAdminIfNecessary();
	}

	@AfterClass
	public static void afeterClass() throws Exception {
		//quitSelenium();
	}

	/**
	 * 启动Jetty服务器, 仅启动一次.
	 */
	protected static void startJettyOnce() throws Exception {
		if (jettyServer == null) {
			jettyServer = JettyFactory.createServer(new URL(baseUrl).getPort(), Start.CONTEXT,
					"src/test/resources/web.xml");
			jettyServer.start();

			logger.info("Jetty Server started");
		}
	}

	/**
	 * 载入测试数据.
	 */
	protected static void reloadSampleData() throws Exception {
		Fixtures.reloadData(dataSource, "/data/sample-data.xml");
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

			//注册在JVM退出时关闭Selenium的钩子.
			Runtime.getRuntime().addShutdownHook(new Thread("Selenium Quit Hook") {
				@Override
				public void run() {
					logger.info("Stoping Selenium");
					s.quit();
				}
			});
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

	private static void buildDataSourceOnce() throws ClassNotFoundException {
		if (dataSource == null) {
			dataSource = new SimpleDriverDataSource();
			dataSource.setDriverClass((Class<? extends Driver>) Class.forName(propertiesLoader
					.getProperty("jdbc.driver")));
			dataSource.setUrl(propertiesLoader.getProperty("jdbc.url"));
			dataSource.setUsername(propertiesLoader.getProperty("jdbc.username"));
			dataSource.setPassword(propertiesLoader.getProperty("jdbc.password"));

		}
	}
}