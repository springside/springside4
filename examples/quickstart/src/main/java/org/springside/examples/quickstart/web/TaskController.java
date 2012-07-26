package org.springside.examples.quickstart.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	public String updateForm(@PathVariable("id") Long id, Model model) {
		model.addAttribute("task", taskManager.getTask(id));
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
	 * 使用@ModelAttribute, 实现Struts2 Preparable二次绑定的效果,先根据form的id从数据库查出Task对象,再把Form提交的内容绑定到该对象上。
	 * 因为仅update()方法的form中有id属性，因此本方法在该方法中执行.
	 */
	@ModelAttribute("task")
	private Task getTask(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			return taskManager.getTask(id);
		}
		return null;
	}

	/**
	 * 不要绑定对象中的id属性.
	 */
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setDisallowedFields("id");
	}
}
