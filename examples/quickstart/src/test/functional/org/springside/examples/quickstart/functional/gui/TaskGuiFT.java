package org.springside.examples.quickstart.functional.gui;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springside.examples.quickstart.data.TaskData;
import org.springside.examples.quickstart.entity.Task;
import org.springside.examples.quickstart.functional.BaseSeleniumTestCase;
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
		assertEquals("Release SpringSide 4.0", s.getTable(table, 0, 0));
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

		assertTrue(s.isTextPresent("创建任务成功"));

		// update
		s.click(By.linkText(task.getTitle()));
		assertEquals(task.getTitle(), s.getValue(By.id("task_title")));

		String newTitle = TaskData.randomTitle();
		s.type(By.id("task_title"), newTitle);
		s.click(By.id("submit_btn"));
		assertTrue(s.isTextPresent("更新任务成功"));

		// search
		s.type(By.name("search_LIKE_title"), newTitle);
		s.click(By.id("search_btn"));
		assertEquals(newTitle, s.getTable(By.id("contentTable"), 0, 0));

		// delete
		s.click(By.linkText("删除"));
		assertTrue("没有成功消息", s.isTextPresent("删除任务成功"));
	}

	@Test
	public void inputInValidateValue() {
		s.open("/task/");
		s.click(By.linkText("创建任务"));
		s.click(By.id("submit_btn"));

		assertEquals("必选字段", s.getText(By.xpath("//fieldset/div/div/span")));
	}
}
