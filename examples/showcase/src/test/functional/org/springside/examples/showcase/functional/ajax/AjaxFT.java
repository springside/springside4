/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.functional.ajax;

import static org.assertj.core.api.Assertions.*;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.springside.examples.showcase.functional.BaseSeleniumTestCase;

/**
 * 测试Ajax Mashup.
 * 
 * @calvin
 */
@Ignore("Ignore the GUI test first")
public class AjaxFT extends BaseSeleniumTestCase {

	@Test
	public void mashup() {
		s.open("/");
		s.click(By.linkText("Web演示"));
		s.click(By.linkText("跨域名Mashup演示"));

		s.click(By.xpath("//input[@value='获取内容']"));
		s.waitForVisible(By.id("mashupContent"));
		assertThat(s.getText(By.id("mashupContent"))).isEqualTo("你好，世界！");
	}
}
