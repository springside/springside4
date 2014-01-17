/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.test.selenium;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * 在出错时截屏的规则.
 * 
 * 在出错时将屏幕保存为png格式的文件，默认路径为项目的target/screensnapshot/测试类名_测试方法名.png
 * 
 * @author calvin
 */
public class SeleniumSnapshotRule extends TestWatcher {

	private final Selenium2 s;

	public SeleniumSnapshotRule(Selenium2 s) {
		this.s = s;
	}

	@Override
	protected void failed(Throwable e, Description description) {
		String basePath = "target/screenshot/";
		String outputFileName = description.getClassName() + "_" + description.getMethodName() + ".png";
		s.snapshot(basePath, outputFileName);
	}
}
