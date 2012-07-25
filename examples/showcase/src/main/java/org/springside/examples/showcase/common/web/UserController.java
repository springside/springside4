package org.springside.examples.showcase.common.web;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springside.examples.showcase.common.entity.User;
import org.springside.examples.showcase.common.service.AccountManager;
import org.springside.modules.web.SpringWebs;

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
	public String update(@Valid @ModelAttribute("updateUser") User user, BindingResult bindingResult, Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return updateForm(model);
		}

		accountManager.saveUser(user);
		redirectAttributes.addFlashAttribute("message", "保存用户成功");
		return "redirect:/common/user/";
	}

	@ModelAttribute("updateUser")
	public User getAccount(NativeWebRequest request) {
		String id = SpringWebs.getPathVariable(request, "id");
		if (id != null) {
			return accountManager.getUser(Long.valueOf(id));
		}
		return null;

	}
}
