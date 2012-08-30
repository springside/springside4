package org.springside.examples.quickstart.web.account;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springside.examples.quickstart.entity.User;
import org.springside.examples.quickstart.service.account.AccountService;

@Controller
@RequestMapping(value = "/admin/user")
public class UserAdminController {

	@Autowired
	private AccountService accountService;

	@RequestMapping(value = "")
	public String list(Model model) {
		List<User> users = accountService.getAllUser();
		model.addAttribute("users", users);
		return "account/adminUserList";
	}

	@RequestMapping(value = "delete/{id}")
	public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		accountService.deleteUser(id);
		redirectAttributes.addFlashAttribute("message", "删除用户成功");
		return "redirect:/admin/user";
	}

	@RequestMapping(value = "update/{id}", method = RequestMethod.GET)
	public String updateForm(@PathVariable("id") Long id, Model model) {
		model.addAttribute("user", accountService.getUser(id));
		return "account/adminUserForm";
	}

	@RequestMapping(value = "update/{id}", method = RequestMethod.POST)
	public String update(@Valid @ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
		accountService.updateUser(user);
		redirectAttributes.addFlashAttribute("message", "更新用户成功");
		return "redirect:/admin/user";
	}

	/**
	 * 使用@ModelAttribute, 实现Struts2 Preparable二次部分绑定的效果,先根据form的id从数据库查出Task对象,再把Form提交的内容绑定到该对象上。
	 * 因为仅update()方法的form中有id属性，因此本方法在该方法中执行.
	 */
	@ModelAttribute("user")
	private User getUser(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			return accountService.getUser(id);
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
