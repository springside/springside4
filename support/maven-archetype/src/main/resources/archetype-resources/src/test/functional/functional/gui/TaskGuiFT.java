#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package ${package}.functional.gui;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import ${package}.data.TaskData;
import ${package}.entity.Task;
import ${package}.functional.BaseSeleniumTestCase;
import org.springside.modules.test.category.Smoke;

/**
 * 任务管理的功能测试, 测试页面JavaScript及主要用户故事流程.
 * 
 * @author calvin
 */
public class TaskGuiFT extends BaseSeleniumTestCase {

	/**
	 * 浏览任务列表.
	 */
	@Test
	@Category(Smoke.class)
	public void viewTaskList() {
		s.open("/task/");
		WebElement table = s.findElement(By.id("contentTable"));
		assertThat(s.getTable(table, 0, 0)).isEqualTo("Release SpringSide 4.0");
	}

	/**
	 * 创建/更新/搜索/删除任务.
	 */
	@Test
	@Category(Smoke.class)
	public void crudTask() {
		s.open("/task/");

		// create
		s.click(By.linkText("创建任务"));

		Task task = TaskData.randomTask();
		s.type(By.id("task_title"), task.getTitle());
		s.click(By.id("submit_btn"));

		assertThat(s.isTextPresent("创建任务成功")).isTrue();

		// update
		s.click(By.linkText(task.getTitle()));
		assertThat(s.getValue(By.id("task_title"))).isEqualTo(task.getTitle());

		String newTitle = TaskData.randomTitle();
		s.type(By.id("task_title"), newTitle);
		s.click(By.id("submit_btn"));
		assertThat(s.isTextPresent("更新任务成功")).isTrue();

		// search
		s.type(By.name("search_LIKE_title"), newTitle);
		s.click(By.id("search_btn"));
		assertThat(s.getTable(By.id("contentTable"), 0, 0)).isEqualTo(newTitle);

		// delete
		s.click(By.linkText("删除"));
		assertThat(s.isTextPresent("删除任务成功")).as("没有成功消息").isTrue();
	}

	@Test
	public void inputInValidateValue() {
		s.open("/task/");
		s.click(By.linkText("创建任务"));
		s.click(By.id("submit_btn"));

		assertThat(s.getText(By.xpath("//fieldset/div/div/span"))).isEqualTo("必选字段");
	}
}
