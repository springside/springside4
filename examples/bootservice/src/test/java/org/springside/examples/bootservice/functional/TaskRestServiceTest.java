/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springside.examples.bootservice.functional;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.client.RestTemplate;
import org.springside.examples.bootservice.BootServiceApplication;
import org.springside.examples.bootservice.domain.Task;
import org.springside.examples.bootservice.domain.User;
import org.springside.modules.test.data.RandomData;

public class TaskRestServiceTest {
	private static ConfigurableApplicationContext context;

	private RestTemplate restTemplate = new RestTemplate();
	private String resourceUrl = "http://localhost:8080/task";

	@BeforeClass
	public static void start() throws Exception {
		Future<ConfigurableApplicationContext> future = Executors.newSingleThreadExecutor().submit(
				new Callable<ConfigurableApplicationContext>() {

					public ConfigurableApplicationContext call() throws Exception {
						return SpringApplication.run(BootServiceApplication.class);
					}
				});
		context = future.get(60, TimeUnit.SECONDS);
	}

	@AfterClass
	public static void stop() {
		if (context != null) {
			context.close();
		}
	}

	@Test
	public void listTask() {
		TaskList tasks = restTemplate.getForObject(resourceUrl, TaskList.class);
		assertThat(tasks).hasSize(5);
		Task firstTask = tasks.get(0);

		assertThat(firstTask.getTitle()).isEqualTo("Spring Boot");
		assertThat(firstTask.getUser().getName()).isEqualTo("Calvin");
	}

	@Test
	public void getTask() {
		Task task = restTemplate.getForObject(resourceUrl + "/{id}", Task.class, 1L);
		assertThat(task.getTitle()).isEqualTo("Spring Boot");
		assertThat(task.getUser().getName()).isEqualTo("Calvin");
	}

	@Test
	public void createUpdateAndDeleteTask() {

		// create
		Task task = randomTask();

		URI createdTaskUri = restTemplate.postForLocation(resourceUrl, task);
		System.out.println(createdTaskUri.toString());
		Task createdTask = restTemplate.getForObject(createdTaskUri, Task.class);
		assertThat(createdTask.getTitle()).isEqualTo(task.getTitle());

		// update
		String id = StringUtils.substringAfterLast(createdTaskUri.toString(), "/");
		task.setId(new Long(id));
		task.setTitle(RandomData.randomName("Task"));

		restTemplate.put(createdTaskUri, task);

		Task updatedTask = restTemplate.getForObject(createdTaskUri, Task.class);
		assertThat(updatedTask.getTitle()).isEqualTo(task.getTitle());

		// delete
		restTemplate.delete(createdTaskUri);
		Task deletedTask = restTemplate.getForObject(createdTaskUri, Task.class);
		assertThat(deletedTask).isNull();
	}

	public static Task randomTask() {
		Task task = new Task();
		task.setTitle(RandomData.randomName("Task"));
		User user = new User(1L);
		task.setUser(user);
		return task;
	}

	// ArrayList<Task>在RestTemplate转换时不好表示，创建一个类来表达它是最简单的。
	private static class TaskList extends ArrayList<Task> {
	}

}
