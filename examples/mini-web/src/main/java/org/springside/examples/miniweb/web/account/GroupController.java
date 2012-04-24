package org.springside.examples.miniweb.web.account;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springside.examples.miniweb.entity.account.Group;
import org.springside.examples.miniweb.entity.account.Permission;
import org.springside.examples.miniweb.service.account.AccountManager;

@Controller
@RequestMapping(value = "/account/group")
public class GroupController {

	@Autowired
	private AccountManager accountManager;

	@RequiresPermissions("group:view")
	@RequestMapping(value = { "list", "" })
	public String list(Model model) {
		List<Group> groups = accountManager.getAllGroup();
		model.addAttribute("groups", groups);
		return "account/groupList";
	}

	@RequiresPermissions("group:edit")
	@RequestMapping(value = "create")
	public String createForm(Model model) {
		model.addAttribute("group", new Group());
		model.addAttribute("allPermissions", Permission.values());
		return "account/groupForm";
	}

	@RequiresPermissions("group:edit")
	@RequestMapping(value = "save")
	public String save(Group group, RedirectAttributes redirectAttributes) {
		accountManager.saveGroup(group);
		redirectAttributes.addFlashAttribute("message", "创建权限组" + group.getName() + "成功");
		return "redirect:/account/group/";
	}

	@RequiresPermissions("group:edit")
	@RequestMapping(value = "delete/{id}")
	public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		accountManager.deleteGroup(id);
		redirectAttributes.addFlashAttribute("message", "删除权限组成功");
		return "redirect:/account/group/";
	}
}
