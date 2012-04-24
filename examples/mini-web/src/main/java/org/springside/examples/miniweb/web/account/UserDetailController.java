package org.springside.examples.miniweb.web.account;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springside.examples.miniweb.entity.account.User;
import org.springside.examples.miniweb.service.account.AccountManager;

/**
 * 使用@ModelAttribute, 实现Struts2 Preparable二次绑定的效果。 
 * 因为@ModelAttribute被默认执行, 而其他的action url中并没有${id}，所以需要独立出一个Controller.
 * 
 * @author calvin
 */
@Controller
@RequestMapping(value = "/account/user/")
public class UserDetailController {

	@Autowired
	private AccountManager accountManager;
	@Autowired
	private GroupListEditor groupListEditor;

	@InitBinder
	public void initBinder(WebDataBinder b) {
		b.registerCustomEditor(List.class, "groupList", groupListEditor);
	}

	@RequiresPermissions("user:edit")
	@RequestMapping(value = "update/{id}")
	public String updateForm(Model model) {
		model.addAttribute("allGroups", accountManager.getAllGroup());
		return "account/userForm";
	}

	@RequiresPermissions("user:edit")
	@RequestMapping(value = "save/{id}")
	public String save(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
		accountManager.saveUser(user);
		redirectAttributes.addFlashAttribute("message", "修改用户" + user.getLoginName() + "成功");
		return "redirect:/account/user/";
	}

	@ModelAttribute("user")
	public User getAccount(@PathVariable("id") Long id) {
		return accountManager.getUser(id);
	}
}
