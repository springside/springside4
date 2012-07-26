package org.springside.examples.quickstart.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springside.examples.quickstart.entity.Task;
import org.springside.examples.quickstart.service.TaskManager;
import org.springside.modules.web.springmvc.OptionalPathVariable;

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
	public String create(@ModelAttribute("newTask") Task newTask, RedirectAttributes redirectAttributes) {
		taskManager.saveTask(newTask);
		redirectAttributes.addFlashAttribute("message", "创建任务成功");
		return "redirect:/task/";
	}

	@RequestMapping(value = "update/{id}")
	public String updateForm(Model model) {
		return "taskForm";
	}

	@RequestMapping(value = "save/{id}")
	public String update(@ModelAttribute("task") Task task, RedirectAttributes redirectAttributes) {
		taskManager.saveTask(task);
		redirectAttributes.addFlashAttribute("message", "更新任务成功");
		return "redirect:/task/";
	}

	@RequestMapping(value = "delete/{id}")
	public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		taskManager.deleteTask(id);
		redirectAttributes.addFlashAttribute("message", "删除任务成功");
		return "redirect:/task/";
	}

	/**
	 * 使用@ModelAttribute, 实现Struts2 Preparable二次绑定的效果,先根据url中的id从数据库查出Task对象,再把Form提交的内容绑定到该对象上。
	 * 本方法仅用于updateForm()/update()，其他方法的url不存在id，因此使用了@OptionalPathVariable。
	 */
	@ModelAttribute("task")
	public Task getTask(@OptionalPathVariable("id") Long id) {
		if (id != null) {
			return taskManager.getTask(Long.valueOf(id));
		}
		return null;
	}
}
