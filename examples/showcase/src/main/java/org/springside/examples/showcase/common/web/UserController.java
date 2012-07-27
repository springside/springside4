package org.springside.examples.showcase.common.web;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springside.examples.showcase.common.entity.User;
import org.springside.examples.showcase.common.service.AccountManager;

import com.google.common.collect.Lists;

@Controller
@RequestMapping(value = "/common/user")
public class UserController {

	@Autowired
	private AccountManager accountManager;

	@RequestMapping(value = { "list", "" })
	public String list(Model model) {
		List<User> users = accountManager.getAllUser();
		model.addAttribute("users", users);
		return "common/userList";
	}

	@RequestMapping(value = "update/{id}")
	public String updateForm(Model model) {
		List<String> allStatus = Lists.newArrayList("enabled", "disabled");
		model.addAttribute("allStatus", allStatus);
		return "common/userForm";
	}

	@RequestMapping(value = "save/{id}")
	public String update(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return updateForm(model);
		}

		accountManager.saveUser(user);
		redirectAttributes.addFlashAttribute("message", "保存用户成功");
		return "redirect:/common/user/";
	}

	/**
	 * 使用@ModelAttribute, 实现Struts2 Preparable二次部分绑定的效果,先根据form的id从数据库查出User对象,再把Form提交的内容绑定到该对象上。
	 * 因为仅update()方法的form中有id属性，因此本方法在该方法中执行.
	 */
	@ModelAttribute("user")
	private User getUser(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			return accountManager.getUser(id);
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
