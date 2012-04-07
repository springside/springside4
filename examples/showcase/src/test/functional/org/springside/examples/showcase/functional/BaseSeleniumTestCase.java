package org.springside.examples.showcase.functional;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static Logger logger = LoggerFactory.getLogger(BaseSeleniumTestCase.class);

	@BeforeClass
	public static void createSeleniumOnce() throws Exception {
		if (s == null) {
			//根据配置创建Selenium driver.
			String driverName = propertiesLoader.getProperty("selenium.driver");

			WebDriver driver = WebDriverFactory.createDriver(driverName);

			s = new Selenium2(driver, baseUrl);

			//注册在JVM退出时关闭Selenium的钩子.
			Runtime.getRuntime().addShutdownHook(new Thread("Selenium Quit Hook") {
				public void run() {
					logger.info("Stoping Selenium");
					s.quit();
				}
			});
		}
	}
}
