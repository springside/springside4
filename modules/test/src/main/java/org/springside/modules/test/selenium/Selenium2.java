/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.springside.modules.test.selenium;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 融合了Selenium 1.0 API与Selenium 2.0的By选择器的API.
 *
 * @author calvin
 */
public class Selenium2 {

	public static final int DEFAULT_WAIT_TIME = 20;
	private final WebDriver driver;
	private final String baseUrl;

	public Selenium2(WebDriver driver, String baseUrl) {
		this.driver = driver;
		this.baseUrl = baseUrl;
		setTimeout(DEFAULT_WAIT_TIME);
	}

	/**
	 * 不设置baseUrl的构造函数, 调用open函数时必须使用绝对路径. 
	 */
	public Selenium2(WebDriver driver) {
		this(driver, "");
	}

	/**
	 * 注册在JVM退出时关闭Selenium的钩子。
	 */
	public void setStopAtShutdown() {
		Runtime.getRuntime().addShutdownHook(new Thread("Selenium Quit Hook") {
			@Override
			public void run() {
				quit();
			}
		});
	}

	// Driver 函數  //
	/**
	 * 打开地址,如果url为相对地址, 自动添加baseUrl.
	 */
	public void open(String url) {
		final String urlToOpen = url.indexOf("://") == -1 ? baseUrl + (!url.startsWith("/") ? "/" : "") + url : url;
		driver.get(urlToOpen);
	}

	/**
	 * 获取当前页面.
	 */
	public String getLocation() {
		return driver.getCurrentUrl();
	}

	/**
	 * 回退历史页面。
	 */
	public void back() {
		driver.navigate().back();
	}

	/**
	 * 刷新页面。
	 */
	public void refresh() {
		driver.navigate().refresh();
	}

	/**
	 * 获取页面标题.
	 */
	public String getTitle() {
		return driver.getTitle();
	}

	/**
	 * 退出Selenium.
	 */
	public void quit() {
		try {
			driver.quit();
		} catch (Exception e) {
			System.err.println("Error happen while quit selenium :" + e.getMessage());
		}
	}

	/**
	 * 设置如果查找不到Element时的默认最大等待时间。
	 */
	public void setTimeout(int seconds) {
		driver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
	}

	/**
	 * 获取WebDriver实例, 调用未封装的函数.
	 */
	public WebDriver getDriver() {
		return driver;
	}

	//Element 函數//

	/**
	 * 查找Element.
	 */
	public WebElement findElement(By by) {
		return driver.findElement(by);
	}

	/**
	 * 查找所有符合条件的Element.
	 */
	public List<WebElement> findElements(By by) {
		return driver.findElements(by);
	}

	/**
	 * 判断页面内是否存在Element.
	 */
	public boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	/**
	 * 判断Element是否可见.
	 */
	public boolean isVisible(By by) {
		return driver.findElement(by).isDisplayed();
	}

	/**
	 * 在Element中输入文本内容.
	 */
	public void type(By by, String text) {
		WebElement element = driver.findElement(by);
		element.clear();
		element.sendKeys(text);
	}

	/**
	 * 点击Element.
	 */
	public void click(By by) {
		driver.findElement(by).click();
	}

	/**
	 * 选中Element.
	 */
	public void check(By by) {
		WebElement element = driver.findElement(by);
		check(element);
	}

	/**
	 * 选中Element.
	 */
	public void check(WebElement element) {
		if (!element.isSelected()) {
			element.click();
		}
	}

	/**
	 * 取消Element的选中.
	 */
	public void uncheck(By by) {
		WebElement element = driver.findElement(by);
		uncheck(element);
	}

	/**
	 * 取消Element的选中.
	 */
	public void uncheck(WebElement element) {
		if (element.isSelected()) {
			element.click();
		}
	}

	/**
	 * 判断Element有否被选中.
	 */
	public boolean isChecked(By by) {
		WebElement element = driver.findElement(by);
		return isChecked(element);
	}

	/**
	 * 判断Element有否被选中.
	 */
	public boolean isChecked(WebElement element) {
		return element.isSelected();
	}

	/**
	 * 返回Select Element,可搭配多种后续的Select操作.
	 * eg. s.getSelect(by).selectByValue(value);
	 */
	public Select getSelect(By by) {
		return new Select(driver.findElement(by));
	}

	/**
	 * 获取Element的文本.
	 */
	public String getText(By by) {
		return driver.findElement(by).getText();
	}

	/**
	 * 获取Input框的value.
	 */
	public String getValue(By by) {
		return getValue(driver.findElement(by));
	}

	/**
	 * 获取Input框的value.
	 */
	public String getValue(WebElement element) {
		return element.getAttribute("value");
	}

	/**
	 * 截屏成png文件，复制到目标文件。源文件存放于临时目录，在JVM退出时自动删除.
	 */
	public void snapshot(String basePath, String outputFileName) {
		File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		File targetFile = new File(basePath, outputFileName);
		try {
			FileUtils.copyFile(srcFile, targetFile);
		} catch (IOException ioe) {
		}
	}

	// WaitFor 函數 //
	/**
	 * 等待页面title的值为title, 使用默认timeout时间.
	 */
	public void waitForTitleIs(String title) {
		waitForCondition(ExpectedConditions.titleIs(title), DEFAULT_WAIT_TIME);
	}

	/**
	 * 等待页面title的值为title, timeout单位为秒.
	 */
	public void waitForTitleIs(String title, int timeout) {
		waitForCondition(ExpectedConditions.titleIs(title), timeout);
	}

	/**
	 * 等待页面title,使用默认timeout时间.
	 */
	public void waitForTitleContains(String title) {
		waitForCondition(ExpectedConditions.titleContains(title), DEFAULT_WAIT_TIME);
	}

	/**
	 * 等待页面title, timeout单位为秒.
	 */
	public void waitForTitleContains(String title, int timeout) {
		waitForCondition(ExpectedConditions.titleContains(title), timeout);
	}

	/**
	 * 等待Element的内容可见, 使用默认timeout时间.
	 */
	public void waitForVisible(By by) {
		waitForCondition(ExpectedConditions.visibilityOfElementLocated(by), DEFAULT_WAIT_TIME);
	}

	/**
	 * 等待Element的内容可见, timeout单位为秒.
	 */
	public void waitForVisible(By by, int timeout) {
		waitForCondition(ExpectedConditions.visibilityOfElementLocated(by), timeout);
	}

	/**
	 * 等待Element的内容为text, 使用默认timeout时间.
	 */
	public void waitForTextPresent(By by, String text) {
		waitForCondition(ExpectedConditions.textToBePresentInElement(by, text), DEFAULT_WAIT_TIME);
	}

	/**
	 * 等待Element的内容为text, timeout单位为秒.
	 */
	public void waitForTextPresent(By by, String text, int timeout) {
		waitForCondition(ExpectedConditions.textToBePresentInElement(by, text), timeout);
	}

	/**
	 * 等待Element的value值为value, 使用默认timeout时间.
	 */
	public void waitForValuePresent(By by, String value) {
		waitForCondition(ExpectedConditions.textToBePresentInElementValue(by, value), DEFAULT_WAIT_TIME);
	}

	/**
	 * 等待Element的value值为value, timeout单位为秒.
	 */
	public void waitForValuePresent(By by, String value, int timeout) {
		waitForCondition(ExpectedConditions.textToBePresentInElementValue(by, value), timeout);
	}

	/**
	 * 等待其他Conditions, timeout单位为秒.
	 * 
	 * @see #waitForTextPresent(By, String, int)
	 * @see ExpectedConditions
	 */
	public void waitForCondition(ExpectedCondition conditon, int timeout) {
		(new WebDriverWait(driver, timeout)).until(conditon);
	}

	// Selenium1.0 函數 //

	/**
	 * 简单判断页面内是否存在文本内容.
	 */
	public boolean isTextPresent(String text) {
		String bodyText = driver.findElement(By.tagName("body")).getText();
		return bodyText.contains(text);
	}

	/**
	 * 取得单元格的内容, 序列从0开始, Selnium1.0的常用函数.
	 */
	public String getTable(WebElement table, int rowIndex, int columnIndex) {
		return table.findElement(By.xpath("//tr[" + (rowIndex + 1) + "]//td[" + (columnIndex + 1) + "]")).getText();
	}

	/**
	 * 取得单元格的内容, 序列从0开始, Selnium1.0的常用函数.
	 */
	public String getTable(By by, int rowIndex, int columnIndex) {
		return getTable(driver.findElement(by), rowIndex, columnIndex);
	}

}
