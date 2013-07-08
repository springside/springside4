package org.springside.modules.test.functional;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springside.modules.test.selenium.WebDriverFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ WebDriverFactory.class, FirefoxDriver.class, InternetExplorerDriver.class, ChromeDriver.class,
		RemoteWebDriver.class })
public class WebDriverFactoryTest {
	@Mock
	FirefoxDriver firefoxDriver;
	@Mock
	InternetExplorerDriver internetExplorerDriver;
	@Mock
	ChromeDriver chromerDriver;
	@Mock
	RemoteWebDriver remoteWebDriver;

	@Test
	public void buildWebDriver() throws Exception {
		MockitoAnnotations.initMocks(this);

		PowerMockito.whenNew(FirefoxDriver.class).withNoArguments().thenReturn(firefoxDriver);
		WebDriver driver = WebDriverFactory.createDriver("firefox");
		assertTrue(driver instanceof FirefoxDriver);

		PowerMockito.whenNew(InternetExplorerDriver.class).withNoArguments().thenReturn(internetExplorerDriver);
		driver = WebDriverFactory.createDriver("ie");
		assertTrue(driver instanceof InternetExplorerDriver);

		PowerMockito.whenNew(ChromeDriver.class).withNoArguments().thenReturn(chromerDriver);
		driver = WebDriverFactory.createDriver("chrome");
		assertTrue(driver instanceof ChromeDriver);

		PowerMockito.whenNew(RemoteWebDriver.class)
				.withArguments(new URL("http://localhost:4444/wd/hub"), DesiredCapabilities.firefox())
				.thenReturn(remoteWebDriver);
		driver = WebDriverFactory.createDriver("remote:localhost:4444:firefox");
		assertTrue(driver instanceof RemoteWebDriver);
	}
}
