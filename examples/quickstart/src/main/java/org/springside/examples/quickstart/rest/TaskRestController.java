package org.springside.examples.quickstart.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springside.examples.quickstart.entity.Task;
import org.springside.examples.quickstart.service.TaskManager;

/**
 * Task的Restful API的Controller.
 * 
 * @author calvin
 *
 */
@Controller
@RequestMapping(value = "/api/task")
public class TaskRestController {

	@Autowired
	private TaskManager taskManager;

	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Task> list() {
		return taskManager.getAllTask();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Task get(@PathVariable("id") Long id) {
		return taskManager.getTask(id);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public Long create(@RequestBody final Task task) {
		taskManager.saveTask(task);
		return task.getId();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(@RequestBody final Task task) {
		taskManager.saveTask(task);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") Long id) {
		taskManager.deleteTask(id);
	}
}
