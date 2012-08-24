package org.springside.examples.quickstart.functional;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springside.modules.test.selenium.Selenium2;
import org.springside.modules.test.selenium.WebDriverFactory;

/**
 * 使用Selenium的功能测试基类.
 * 
 * 在BaseFunctionalTestCase的基础上，在整个测试期间仅启动一次Selenium.
 * 
 * @author calvin
 */
public class BaseSeleniumTestCase extends BaseFunctionalTestCase {

	protected static Selenium2 s;

	@BeforeClass
	public static void init() throws Exception {
		createSeleniumOnce();
		loginAsUserIfNecessary();
	}

	/**
	 * 创建Selenium，仅创建一次.
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
	protected static void loginAsUserIfNecessary() {
		s.open("/task");

		if ("QuickStart示例:登录页".equals(s.getTitle())) {
			s.type(By.name("username"), "user");
			s.type(By.name("password"), "user");
			s.check(By.name("rememberMe"));
			s.click(By.id("submit_btn"));
			assertEquals("QuickStart示例:任务管理", s.getTitle());
		}
	}

}
