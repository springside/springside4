package org.springside.examples.miniweb.web.account;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springside.examples.miniweb.entity.account.Group;
import org.springside.examples.miniweb.entity.account.User;
import org.springside.examples.miniweb.service.ServiceException;
import org.springside.examples.miniweb.service.account.AccountManager;
import org.springside.examples.miniweb.web.CrudActionSupport;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;
import org.springside.modules.orm.PropertyFilter;

/**
 * 用户管理Action.
 * 
 * 使用Struts2 convention-plugin annotation定义Action参数.
 * 演示带分页的管理界面.
 * 
 * @author calvin
 */
//定义URL映射对应/account/user.action
@Namespace("/account")
//定义名为reload的result重定向到user.action, 其他result则按照convention默认.
@Results({ @Result(name = CrudActionSupport.RELOAD, location = "user.action", type = "redirect") })
public class UserAction extends CrudActionSupport<User> {

	private static final long serialVersionUID = 8683878162525847072L;

	private AccountManager accountManager;

	//-- 页面属性 --//
	private Long id;
	private User entity;
	private Page<User> page = new Page<User>();
	private List<Long> checkedGroupIds; //页面中钩选的权限组id列表

	//-- ModelDriven 与 Preparable函数 --//
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public User getModel() {
		return entity;
	}

	@Override
	protected void prepareModel() throws Exception {
		if (id != null) {
			entity = accountManager.getUser(id);
		} else {
			entity = new User();
		}
	}

	//-- CRUD Action 函数 --//
	@Override
	public String list() throws Exception {
		List<PropertyFilter> filters = null;//PropertyFilter.buildFromHttpRequest(Struts2Utils.getRequest());

		page.setPageSize(5);
		//设置默认排序方式
		if (!page.isOrderBySetted()) {
			page.setOrderBy("id");
			page.setOrderDir(PageRequest.Sort.ASC);
		}
		page = accountManager.searchUser(page, filters);
		return SUCCESS;
	}

	@Override
	public String input() throws Exception {
		checkedGroupIds = entity.getGroupIds();
		return INPUT;
	}

	@Override
	public String save() throws Exception {
		//根据页面上的checkbox选择 整合User的Groups Set
		CrudActionSupport.mergeByIds(entity.getGroupList(), checkedGroupIds, Group.class);

		accountManager.saveUser(entity);
		addActionMessage("保存用户成功");
		return RELOAD;
	}

	@Override
	public String delete() throws Exception {
		try {
			accountManager.deleteUser(id);
			addActionMessage("删除用户成功");
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
			addActionMessage("删除用户失败");
		}
		return RELOAD;
	}

	//-- 其他Action函数 --//
	/**
	 * 支持使用Jquery.validate Ajax检验用户名是否重复.
	 */
	public String checkLoginName() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String newLoginName = request.getParameter("loginName");
		String oldLoginName = request.getParameter("oldLoginName");
		/*
				if (accountManager.isLoginNameUnique(newLoginName, oldLoginName)) {
					Struts2Utils.renderText("true");
				} else {
					Struts2Utils.renderText("false");
				}
			*/
		//因为直接输出内容而不经过jsp,因此返回null.
		return null;
	}

	//-- 页面属性访问函数 --//
	/**
	 * list页面显示用户分页列表.
	 */
	public Page<User> getPage() {
		return page;
	}

	/**
	 * input页面显示所有权限组列表.
	 */
	public List<Group> getAllGroupList() {
		return accountManager.getAllGroup();
	}

	/**
	 * input页面显示用户拥有的权限组.
	 */
	public List<Long> getCheckedGroupIds() {
		return checkedGroupIds;
	}

	/**
	 * input页面提交用户拥有的权限组.
	 */
	public void setCheckedGroupIds(List<Long> checkedGroupIds) {
		this.checkedGroupIds = checkedGroupIds;
	}

	@Autowired
	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}
}
