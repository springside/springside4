package org.springside.examples.miniweb.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.examples.miniweb.entity.Task;
import org.springside.examples.miniweb.service.TaskManager;

@Controller
@RequestMapping(value = "/api/task")
public class TaskRestController {

	@Autowired
	private TaskManager taskManager;

	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	List<Task> list(Model model) {
		return taskManager.getAllTask();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	Task get(@PathVariable("id") Long id) {
		return taskManager.getTask(id);
	}
}
