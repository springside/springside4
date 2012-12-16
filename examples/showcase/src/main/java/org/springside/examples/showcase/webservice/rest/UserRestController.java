package org.springside.examples.showcase.webservice.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    // /api/v1/user/1.json /api/v1/user/1.xml rest api风格
    // 注意：不用再加@ResponseBody注解
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public UserDTO getAsXxx(@PathVariable("id") Long id) {
		User user = accountService.getUser(id);
		return bindDTO(user);
	}

	private UserDTO bindDTO(User user) {
		UserDTO dto = BeanMapper.map(user, UserDTO.class);
		//补充Dozer不能自动绑定的属性
		dto.setTeamId(user.getTeam().getId());
		return dto;
	}
}
