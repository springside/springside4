package org.springside.examples.quickstart.functional.rest;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.web.client.RestTemplate;
import org.springside.examples.quickstart.data.TaskData;
import org.springside.examples.quickstart.entity.Task;
import org.springside.examples.quickstart.functional.BaseFunctionalTestCase;
import org.springside.modules.test.category.Smoke;

/**
 * 任务管理的功能测试, 测试页面JavaScript及主要用户故事流程.
 * 
 * @author calvin
 */
public class TaskRestIT extends BaseFunctionalTestCase {

	private final RestTemplate restTemplate = new RestTemplate();

	private static class TaskList extends ArrayList<Task> {
	};

	private static String resoureUrl;

	@BeforeClass
	public static void initUrl() {
		resoureUrl = baseUrl + "/api/task";
	}

	/**
	 * 查看任务列表.
	 */
	@Test
	@Category(Smoke.class)
	public void listTasks() {
		TaskList tasks = restTemplate.getForObject(resoureUrl, TaskList.class);
		assertEquals(5, tasks.size());
		assertEquals("Study PlayFramework 2.0", tasks.get(0).getTitle());
	}

	/**
	 * 获取任务.
	 */
	@Test
	@Category(Smoke.class)
	public void getTask() {
		Long id = 1L;
		Task task = restTemplate.getForObject(resoureUrl + "/{id}", Task.class, id);
		assertEquals("Study PlayFramework 2.0", task.getTitle());
	}

	/**
	 * 创建/更新/删除任务.
	 */
	@Test
	@Category(Smoke.class)
	public void createUpdateAndDeleteTask() {

		//create
		Task task = TaskData.randomTask();

		URI taskUri = restTemplate.postForLocation(resoureUrl, task);

		assertEquals(resoureUrl + "/6", taskUri.toString());

		//update
		String id = StringUtils.substringAfterLast(taskUri.toString(), "/");
		String newTitle = TaskData.randomTitle();
		task.setId(new Long(id));
		task.setTitle(newTitle);

		restTemplate.put(taskUri, task);

		Task updatedTask = restTemplate.getForObject(taskUri, Task.class);
		assertEquals(newTitle, updatedTask.getTitle());

		//delete
		restTemplate.delete(taskUri);

		Task deletedTask = restTemplate.getForObject(taskUri, Task.class);
		assertNull(deletedTask);
	}
}
