package org.springside.examples.miniweb.functional.gui;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springside.examples.miniweb.data.TaskData;
import org.springside.examples.miniweb.entity.Task;
import org.springside.examples.miniweb.functional.BaseFunctionalTestCase;
import org.springside.modules.test.category.Smoke;

/**
 * 任务管理的功能测试, 测试页面JavaScript及主要用户故事流程.
 * 
 * @author calvin
 */
public class TaskGuiIT extends BaseFunctionalTestCase {

	/**
	 * 浏览用户列表.
	 */
	@Test
	@Category(Smoke.class)
	public void viewTaskList() {
		s.open("/task/");
		WebElement table = s.findElement(By.id("contentTable"));
		assertEquals("Study PlayFramework 2.0", s.getTable(table, 0, 0));
	}

	/**
	 * 创建并更新任务.
	 */
	@Test
	@Category(Smoke.class)
	public void createAndUpdateTask() {
		s.open("/task/");

		//create
		s.click(By.linkText("创建任务"));

		Task task = TaskData.randomTask();
		s.type(By.id("task.title"), task.getTitle());
		s.click(By.id("submit"));

		assertTrue(s.isTextPresent("创建任务成功"));

		//update
		s.click(By.linkText(task.getTitle()));
		assertEquals(task.getTitle(), s.getValue(By.id("task.title")));

		String newTitle = TaskData.randomTitle();
		s.type(By.id("task.title"), newTitle);
		s.click(By.id("submit"));
		assertTrue(s.isTextPresent("更新任务成功"));
	}

	@Test
	public void inputInValidateValue() {
		s.open("/task/");
		s.click(By.linkText("创建任务"));
		s.click(By.id("submit"));

		assertEquals("必选字段", s.getText(By.xpath("//fieldset/div/div/label")));
	}

}
