package org.springside.examples.miniweb.web.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springside.examples.miniweb.entity.account.Group;
import org.springside.examples.miniweb.entity.account.Permission;
import org.springside.examples.miniweb.service.account.AccountManager;

@Controller
@RequestMapping(value = "/account/group/")
public class GroupDetailController {

	private AccountManager accountManager;

	@RequestMapping(value = "update/{id}")
	public String updateForm(Model model) {
		model.addAttribute("allPermissions", Permission.values());
		return "account/groupForm";
	}

	@RequestMapping(value = "save/{id}")
	public String save(@ModelAttribute("group") Group group, RedirectAttributes redirectAttributes) {
		accountManager.saveGroup(group);
		redirectAttributes.addFlashAttribute("message", "修改权限组" + group.getName() + "成功");
		return "redirect:/account/group/";
	}

	@ModelAttribute("group")
	public Group getGroup(@PathVariable("id") Long id) {
		return accountManager.getGroup(id);
	}

	@Autowired
	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}
}
