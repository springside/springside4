package org.springside.examples.showcase.functional;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.openqa.selenium.WebDriver;
import org.springside.modules.test.functional.Selenium2;
import org.springside.modules.test.functional.WebDriverFactory;

/**
 * 功能测试基类.
 * 
 * 在BaseFunctionalTestCase的基础上，加入对Selenium WebDriver的启动和关闭.
 * 
 * @author calvin
 */
@Ignore
public class BaseSeleniumTestCase extends BaseFunctionalTestCase {

	protected static Selenium2 s;

	@BeforeClass
	public static void startSelenium() throws Exception {
		String driverName = propertiesLoader.getProperty("selenium.driver");

		WebDriver driver = WebDriverFactory.createDriver(driverName);

		s = new Selenium2(driver, baseUrl);
	}

	@AfterClass
	public static void stopSelenium() throws Exception {
		s.quit();
	}
}
