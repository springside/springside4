package org.springside.examples.showcase.functional;

import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.openqa.selenium.WebDriver;
import org.springside.examples.showcase.Start;
import org.springside.modules.test.functional.Selenium2;
import org.springside.modules.test.functional.WebDriverFactory;
import org.springside.modules.utils.PropertiesLoader;

/**
 * 功能测试基类.
 * 
 * 在整个测试期间启动一次Jetty Server, 并在每个TestCase执行前中重新载入默认数据.
 * 
 * @author calvin
 */
@Ignore
public class BaseSeleniumTestCase extends BaseFunctionalTestCase {

	protected static Selenium2 s;

	@BeforeClass
	public static void startSelenium() throws Exception {
		createSelenium();
	}

	@AfterClass
	public static void stopSelenium() throws Exception {
		quitSelenium();
	}

	/**
	 * 创建Selenium.
	 */
	protected static void createSelenium() throws Exception {
		Properties props = PropertiesLoader.loadProperties("classpath:/application.test.properties",
				"classpath:/application.test-local.properties");

		WebDriver driver = WebDriverFactory.createDriver(props.getProperty("selenium.driver"));
		s = new Selenium2(driver, Start.BASE_URL);
	}

	/**
	 * 关闭Selenium.
	 */
	protected static void quitSelenium() {
		s.quit();
	}
}
