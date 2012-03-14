package org.springside.examples.showcase.functional;

import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static Logger logger = LoggerFactory.getLogger(BaseFunctionalTestCase.class);

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
		WebDriver driver = null;
		Properties props = PropertiesLoader.loadProperties("classpath:/application.test.properties",
				"classpath:/application.test-local.properties");
		String driverName = props.getProperty("selenium.driver");
		try {
			driver = WebDriverFactory.createDriver(driverName);
		} catch (Exception e) {
			logger.error(driverName + " create fail, use HtmlUnitDriver instead");
			driver = new HtmlUnitDriver();
		}

		s = new Selenium2(driver, Start.TEST_BASE_URL);
	}

	/**
	 * 关闭Selenium.
	 */
	protected static void quitSelenium() {
		s.quit();
	}
}
