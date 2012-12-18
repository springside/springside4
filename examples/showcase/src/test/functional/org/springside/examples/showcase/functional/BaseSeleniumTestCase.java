package org.springside.examples.showcase.functional;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.springside.modules.test.selenium.Selenium2;
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
	public TestRule watcher = new TestWatcher() {
		@Override
		protected void failed(Throwable e, Description description) {
			File scrFile = s.snapshot();
			String scrFilename = description.getClassName() + "_" + description.getMethodName() + ".png";
			File outputFile = new File("target/screenshot/", scrFilename);
			try {
				FileUtils.copyFile(scrFile, outputFile);
			} catch (IOException ioe) {
			}
		}
	};

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
