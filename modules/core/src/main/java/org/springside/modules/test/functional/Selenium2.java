/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.springside.modules.test.functional;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebElement;
import org.springside.modules.utils.Threads;

import com.thoughtworks.selenium.Selenium;

/**
 * 融合了Selenium 1.0 API与Selenium 2.0的By选择器的API.
 *
 * @author calvin
 */
public class Selenium2 {

	public static final int DEFAULT_TIMEOUT = 5000;
	public static final int DEFAULT_PAUSE_TIME = 250;

	private WebDriver driver;
	private Selenium selenium;
	private int defaultTimeout = DEFAULT_TIMEOUT;

	public Selenium2(WebDriver driver, String baseUrl) {
		this.driver = driver;
		this.selenium = new WebDriverBackedSelenium(driver, baseUrl);
	}

	/**
	 * 不设置baseUrl的构造函数, 调用open函数时必须使用绝对路径. 
	 */
	public Selenium2(WebDriver driver) {
		this(driver, "");
	}

	/**
	 * 打开地址,如果url为相对地址, 自动添加baseUrl.
	 * @param url
	 */
	public void open(String url) {
		selenium.open(url);
		waitForPageToLoad();
	}

	/**
	 * 获取页面标题.
	 */
	public String getTitle() {
		return driver.getTitle();
	}

	/**
	 * 获取页面地址.
	 */
	public String getLocation() {
		return driver.getCurrentUrl();
	}

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
	 * 判断页面内是否存在文本内容.
	 */
	public boolean isTextPresent(String text) {
		return selenium.isTextPresent(text);
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
	public boolean isDisplayed(By by) {
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
	 * 点击Element, 跳转到新页面.
	 */
	public void clickTo(By by) {
		driver.findElement(by).click();
		waitForPageToLoad();
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
		return element.isSelected();
	}

	/**
	 * 判断Element有否被选中.
	 */
	public boolean isChecked(WebElement element) {
		return element.isSelected();
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
		return driver.findElement(by).getAttribute("value");
	}

	/**
	 * 获取Input框的value.
	 */
	public String getValue(WebElement element) {
		return element.getAttribute("value");
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

	/**
	 * 等待页面载入完成, timeout时间为defaultTimeout的值.
	 */
	public void waitForPageToLoad() {
		waitForPageToLoad(defaultTimeout);
	}

	/**
	 * 等待页面载入完成, timeout单位为毫秒.
	 */
	public void waitForPageToLoad(int timeout) {
		selenium.waitForPageToLoad(String.valueOf(timeout));
	}

	/**
	 * 等待Element的内容展现, timeout单位为毫秒.
	 */
	public void waitForVisible(By by, int timeout) {
		long timeoutTime = System.currentTimeMillis() + timeout;
		while (System.currentTimeMillis() < timeoutTime) {
			if (isDisplayed(by)) {
				return;
			}
			Threads.sleep(DEFAULT_PAUSE_TIME);
		}
		throw new RuntimeException("waitForVisible timeout");
	}

	/**
	 * 退出Selenium.
	 */
	public void quit() {
		driver.close();
		driver.quit();
	}

	/**
	 * 获取WebDriver实例, 调用未封装的函数.
	 */
	public WebDriver getDriver() {
		return driver;
	}

	/**
	 * 获取Selenium实例,调用未封装的函数.
	 */
	public Selenium getSelenium() {
		return selenium;
	}

	/**
	 * 设置默认页面超时.
	 */
	public void setDefaultTimeout(int defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
	}
}
