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

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import org.springside.examples.bootservice.BootMoreApplication;
import org.springside.examples.bootservice.domain.Task;
import org.springside.examples.bootservice.domain.User;
import org.springside.modules.test.data.RandomData;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BootMoreApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
@DirtiesContext
public class TaskRestServiceTest {

	@Value("${local.server.port}")
	private int port;

	private RestTemplate restTemplate;
	private String resourceUrl;

	@Before
	public void setup() {
		restTemplate = new TestRestTemplate();
		resourceUrl = "http://localhost:" + port + "/task";
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
