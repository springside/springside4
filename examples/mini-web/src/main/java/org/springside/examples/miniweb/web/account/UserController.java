package org.springside.examples.miniweb.web.account;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springside.examples.miniweb.entity.account.User;
import org.springside.examples.miniweb.service.account.AccountManager;

/**
 * Urls:
 * List   page        : GET  /account/user/
 * Create page        : GET  /account/user/create
 * Create action      : POST /account/user/save
 * Update page        : GET  /account/user/update/{id}
 * Update action      : POST /account/user/save/{id}
 * Delete action      : POST /account/user/delete/{id}
 * CheckLoginName ajax: GET  /account/user/checkLoginName?oldLoginName=a&loginName=b
 * 
 * @author calvin
 *
 */
@Controller
@RequestMapping(value = "/account/user")
public class UserController {

	@Autowired
	private AccountManager accountManager;

	@Autowired
	private GroupListEditor groupListEditor;

	@InitBinder
	public void initBinder(WebDataBinder b) {
		b.registerCustomEditor(List.class, "groupList", groupListEditor);
	}

	@RequiresPermissions("user:view")
	@RequestMapping(value = { "list", "" })
	public String list(Model model) {
		List<User> users = accountManager.getAllUser();
		model.addAttribute("users", users);
		return "account/userList";
	}

	@RequiresPermissions("user:edit")
	@RequestMapping(value = "create")
	public String createForm(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("allGroups", accountManager.getAllGroup());
		return "account/userForm";
	}

	@RequiresPermissions("user:edit")
	@RequestMapping(value = "save")
	public String save(User user, RedirectAttributes redirectAttributes) {
		accountManager.saveUser(user);
		redirectAttributes.addFlashAttribute("message", "创建用户" + user.getLoginName() + "成功");
		return "redirect:/account/user/";
	}

	@RequiresPermissions("user:edit")
	@RequestMapping(value = "delete/{id}")
	public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		accountManager.deleteUser(id);
		redirectAttributes.addFlashAttribute("message", "删除用户成功");
		return "redirect:/account/user/";
	}

	@RequiresPermissions("user:edit")
	@RequestMapping(value = "checkLoginName")
	@ResponseBody
	public String checkLoginName(@RequestParam("oldLoginName") String oldLoginName,
			@RequestParam("loginName") String loginName) {
		if (loginName.equals(oldLoginName)) {
			return "true";
		} else if (accountManager.findUserByLoginName(loginName) == null) {
			return "true";
		}

		return "false";
	}
}
