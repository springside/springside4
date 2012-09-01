package org.springside.examples.quickstart.web.task;

import java.util.Map;

import javax.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springside.examples.quickstart.entity.Task;
import org.springside.examples.quickstart.entity.User;
import org.springside.examples.quickstart.service.account.ShiroDbRealm.ShiroUser;
import org.springside.examples.quickstart.service.task.TaskService;

import com.google.common.collect.Maps;

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
	private TaskService taskService;

	private static final int PAGE_SIZE = 3;

	private static Map<String, String> sortTypes = Maps.newLinkedHashMap();
	static {
		sortTypes.put("auto", "自动");
		sortTypes.put("title", "标题");
	}

	@RequestMapping(value = "")
	public String list(@RequestParam(value = "sortType", defaultValue = "auto") String sortType,
			@RequestParam(value = "page", defaultValue = "1") int pageNumber, Model model) {
		Long userId = getCurrentUserId();
		Page<Task> tasks = taskService.getUserTask(userId, pageNumber, PAGE_SIZE, sortType);
		model.addAttribute("tasks", tasks);

		model.addAttribute("sortType", sortType);
		model.addAttribute("sortTypes", sortTypes);

		return "task/taskList";
	}

	@RequestMapping(value = "create")
	public String createForm(Model model) {
		model.addAttribute("task", new Task());
		return "task/taskForm";
	}

	@RequestMapping(value = "save")
	public String create(@Valid Task newTask, RedirectAttributes redirectAttributes) {
		User user = new User(getCurrentUserId());
		newTask.setUser(user);

		taskService.saveTask(newTask);
		redirectAttributes.addFlashAttribute("message", "创建任务成功");
		return "redirect:/task/";
	}

	@RequestMapping(value = "update/{id}")
	public String updateForm(@PathVariable("id") Long id, Model model) {
		model.addAttribute("task", taskService.getTask(id));
		return "task/taskForm";
	}

	@RequestMapping(value = "save/{id}")
	public String update(@Valid Task task, RedirectAttributes redirectAttributes) {
		taskService.saveTask(task);
		redirectAttributes.addFlashAttribute("message", "更新任务成功");
		return "redirect:/task/";
	}

	@RequestMapping(value = "delete/{id}")
	public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		taskService.deleteTask(id);
		redirectAttributes.addFlashAttribute("message", "删除任务成功");
		return "redirect:/task/";
	}

	/**
	 * 取出Shiro中的当前用户Id.
	 */
	private Long getCurrentUserId() {
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		return user.id;
	}
}
