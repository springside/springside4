package org.springside.examples.quickstart.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springside.examples.quickstart.entity.Task;
import org.springside.examples.quickstart.service.TaskManager;

/**
 * Task管理的Controller, 使用Restful风格的Urls:
 * 
 * List   page        : GET  /task/
 * Create page        : GET  /task/create
 * Create action      : POST /task/save
 * Update page        : GET  /task/update/{id}
 * Update action      : POST /task/save/{id}
 * Delete action      : POST /task/delete/{id}
 * 
 * 其中Update page/Update action在TaskDetailController, 其余在本Controller。
 * 
 * @author calvin
 */
@Controller
@RequestMapping(value = "/task")
public class TaskController {

	@Autowired
	private TaskManager taskManager;

	@RequestMapping(value = { "list", "" })
	public String list(Model model) {
		List<Task> tasks = taskManager.getAllTask();
		model.addAttribute("tasks", tasks);
		return "taskList";
	}

	@RequestMapping(value = "create")
	public String createForm(Model model) {
		model.addAttribute("task", new Task());
		return "taskForm";
	}

	@RequestMapping(value = "save")
	public String save(Task task, RedirectAttributes redirectAttributes) {
		taskManager.saveTask(task);
		redirectAttributes.addFlashAttribute("message", "创建任务成功");
		return "redirect:/task/";
	}

	@RequestMapping(value = "delete/{id}")
	public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		taskManager.deleteTask(id);
		redirectAttributes.addFlashAttribute("message", "删除任务成功");
		return "redirect:/task/";
	}
}
