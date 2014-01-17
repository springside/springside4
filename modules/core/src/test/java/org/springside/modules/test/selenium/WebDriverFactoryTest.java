/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.test.selenium;

import static org.assertj.core.api.Assertions.*;

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
		assertThat(driver).isInstanceOf(FirefoxDriver.class);

		PowerMockito.whenNew(InternetExplorerDriver.class).withNoArguments().thenReturn(internetExplorerDriver);
		driver = WebDriverFactory.createDriver("ie");
		assertThat(driver).isInstanceOf(InternetExplorerDriver.class);

		PowerMockito.whenNew(ChromeDriver.class).withNoArguments().thenReturn(chromerDriver);
		driver = WebDriverFactory.createDriver("chrome");
		assertThat(driver).isInstanceOf(ChromeDriver.class);

		PowerMockito.whenNew(RemoteWebDriver.class)
				.withArguments(new URL("http://localhost:4444/wd/hub"), DesiredCapabilities.firefox())
				.thenReturn(remoteWebDriver);
		driver = WebDriverFactory.createDriver("remote:localhost:4444:firefox");
		assertThat(driver).isInstanceOf(RemoteWebDriver.class);
	}
}
