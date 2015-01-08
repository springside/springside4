package org.springside.examples.bootservice.rest;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springside.examples.bootservice.domain.Task;
import org.springside.examples.bootservice.service.TaskService;
import org.springside.modules.web.MediaTypes;

// Spring MVC Controller的标识
@RestController
@RequestMapping(value = "/task")
public class TaskRestController {

	@Autowired
	private CounterService counterService;

	@Autowired
	private TaskService taskService;

	@RequestMapping(method = RequestMethod.GET, produces = MediaTypes.JSON_UTF_8)
	public List<Task> list() {
		counterService.increment("task.list");
		return taskService.getAllTask();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaTypes.JSON_UTF_8)
	public Task get(@PathVariable("id") Long id) {
		counterService.increment("task.get");
		return taskService.getTask(id);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaTypes.JSON)
	public ResponseEntity<?> create(@RequestBody Task task,
			UriComponentsBuilder uriBuilder) {
		counterService.increment("task.create");
		// 保存任务
		taskService.saveTask(task);

		// 按照Restful风格约定, 创建指向新任务的url, 也可以直接返回id或对象.
		HttpHeaders headers = createLocation(uriBuilder, "/task/" + task.id);

		return new ResponseEntity(headers, HttpStatus.CREATED);
	}

	private HttpHeaders createLocation(UriComponentsBuilder uriBuilder,
			String path) {
		URI uri = uriBuilder.path(path).build().toUri();
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uri);
		return headers;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaTypes.JSON)
	// 按Restful风格约定, 返回204状态码, 无内容, 也可以返回200状态码.
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(@RequestBody Task task) {
		counterService.increment("task.update");
		taskService.saveTask(task);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") Long id) {
		counterService.increment("task.delete");
		taskService.deleteTask(id);
	}
}
