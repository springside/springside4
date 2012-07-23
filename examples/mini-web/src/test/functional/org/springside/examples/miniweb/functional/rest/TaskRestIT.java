package org.springside.examples.miniweb.functional.rest;

import static org.junit.Assert.*;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springside.examples.miniweb.entity.Task;
import org.springside.examples.miniweb.functional.BaseFunctionalTestCase;
import org.springside.modules.test.category.Smoke;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

/**
 * 任务管理的功能测试, 测试页面JavaScript及主要用户故事流程.
 * 
 * @author calvin
 */
public class TaskRestIT extends BaseFunctionalTestCase {

	private WebResource client;

	private final GenericType<List<Task>> taskListType = new GenericType<List<Task>>() {
	};

	@Before
	public void setupClient() {
		client = Client.create().resource(baseUrl);
	}

	/**
	 * 查看任务列表.
	 */
	@Test
	@Category(Smoke.class)
	public void listTasks() {
		WebResource wr = client.path("/api/task");
		List<Task> tasks = wr.get(taskListType);

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
		Task task = client.path("/api/task/" + id).accept(MediaType.APPLICATION_JSON).get(Task.class);
		assertEquals("Study PlayFramework 2.0", task.getTitle());
	}
}
