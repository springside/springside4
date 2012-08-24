/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.springside.modules.test.selenium;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.util.Assert;

/**
 * 创建WebDriver的工厂类, 支持主要的firefox,ie和remote三种driver.
 * 
 * @author calvin
 */
public class WebDriverFactory {

	/**
	 * 根据driverName创建各种WebDriver的简便方法.
	 * 
	 * 当持续集成服务器安装在非Windows机器上, 没有IE浏览器与XWindows时, 需要使用remote driver调用远程的Windows机器.
	 * drivername如remote:192.168.0.2:4444:firefox, 此时要求远程服务器在http://192.168.0.2:4444/wd/hub上启动selenium remote服务.
	 * @throws MalformedURLException 
	 */
	public static WebDriver createDriver(String driverName) {
		WebDriver driver = null;

		if (BrowserType.firefox.name().equals(driverName)) {
			driver = new FirefoxDriver();
		} else if (BrowserType.ie.name().equals(driverName)) {
			driver = new InternetExplorerDriver();
		} else if (BrowserType.chrome.name().equals(driverName)) {
			driver = new ChromeDriver();
		} else if (BrowserType.htmlunit.name().equals(driverName)) {
			driver = new HtmlUnitDriver(true);
		} else if (driverName.startsWith(BrowserType.remote.name())) {
			String[] params = driverName.split(":");
			Assert.isTrue(params.length == 4,
					"Remote driver is not right, accept format is \"remote:localhost:4444:firefox\", but the input is\""
							+ driverName + "\"");

			String remoteHost = params[1];
			String remotePort = params[2];
			String driverType = params[3];
			DesiredCapabilities cap = null;

			if (BrowserType.firefox.name().equals(driverType)) {
				cap = DesiredCapabilities.firefox();
			} else if (BrowserType.ie.name().equals(driverType)) {
				cap = DesiredCapabilities.internetExplorer();
			} else if (BrowserType.chrome.name().equals(driverType)) {
				cap = DesiredCapabilities.chrome();
			}

			try {
				driver = new RemoteWebDriver(new URL("http://" + remoteHost + ":" + remotePort + "/wd/hub"), cap);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}

		Assert.notNull(driver, "Driver could be found by name:" + driverName);

		return driver;
	}

	public enum BrowserType {
		firefox, ie, chrome, htmlunit, remote
	}
}
