package org.springside.examples.showcase.common.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springside.examples.showcase.common.entity.User;
import org.springside.examples.showcase.common.service.AccountManager;

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
}
