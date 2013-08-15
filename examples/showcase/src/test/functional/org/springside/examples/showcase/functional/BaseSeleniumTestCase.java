package org.springside.examples.showcase.functional;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.openqa.selenium.WebDriver;
import org.springside.modules.test.selenium.Selenium2;
import org.springside.modules.test.selenium.SeleniumSnapshotRule;
import org.springside.modules.test.selenium.WebDriverFactory;

/**
 * 使用Selenium的功能测试基类.
 * 
 * 在BaseFunctionalTestCase的基础上，在整个测试期间启动一次Selenium.
 * 
 * @author calvin
 */
public class BaseSeleniumTestCase extends BaseFunctionalTestCase {

	protected static Selenium2 s;

	//出错时截屏的规则
	@Rule
	public TestRule snapshotRule = new SeleniumSnapshotRule(s);

	@BeforeClass
	public static void createSeleniumOnce() throws Exception {
		if (s == null) {
			//根据配置创建Selenium driver.
			String driverName = propertiesLoader.getProperty("selenium.driver");

			WebDriver driver = WebDriverFactory.createDriver(driverName);

			s = new Selenium2(driver, baseUrl);
			s.setStopAtShutdown();
		}
	}
}
