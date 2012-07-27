package org.springside.examples.showcase.web;

import java.util.List;

import javax.validation.Valid;

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
import org.springside.examples.showcase.entity.User;
import org.springside.examples.showcase.service.AccountManager;

import com.google.common.collect.Lists;

@Controller
@RequestMapping(value = "/account/user")
public class UserController {

	@Autowired
	private AccountManager accountManager;

	@RequestMapping(value = { "list", "" })
	public String list(Model model) {
		List<User> users = accountManager.getAllUser();
		model.addAttribute("users", users);
		return "account/userList";
	}

	@RequestMapping(value = "update/{id}")
	public String updateForm(@PathVariable("id") Long id, Model model) {
		model.addAttribute("user", accountManager.getUser(id));

		List<String> allStatus = Lists.newArrayList("enabled", "disabled");
		model.addAttribute("allStatus", allStatus);
		return "account/userForm";
	}

	@RequestMapping(value = "save/{id}")
	public String update(@Valid @ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
		accountManager.saveUser(user);
		redirectAttributes.addFlashAttribute("message", "保存用户成功");
		return "redirect:/account/user/";
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
