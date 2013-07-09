package org.springside.examples.showcase.webservice.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.examples.showcase.entity.User;
import org.springside.examples.showcase.service.AccountEffectiveService;
import org.springside.modules.mapper.BeanMapper;

@Controller
@RequestMapping(value = "/api/v1/user")
public class UserRestController {
	@Autowired
	private AccountEffectiveService accountService;

	/**
	 * 基于ContentNegotiationManager,根据URL的后缀渲染不同的格式
	 * eg. /api/v1/user/1.xml 返回xml
	 *     /api/v1/user/1.json 返回json
	 *     /api/v1/user/1 返回xml(why?)
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public UserDTO getUser(@PathVariable("id") Long id) {
		User user = accountService.getUser(id);

		// 使用Dozer转换DTO类，并补充Dozer不能自动绑定的属性
		UserDTO dto = BeanMapper.map(user, UserDTO.class);
		dto.setTeamId(user.getTeam().getId());
		return dto;
	}
}
