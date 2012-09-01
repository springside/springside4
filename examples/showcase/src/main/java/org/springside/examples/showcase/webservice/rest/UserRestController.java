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

	@RequestMapping(value = "/{id}.xml", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	@ResponseBody
	public UserDTO getAs(@PathVariable("id") Long id) {
		User user = accountService.getUser(id);
		return BeanMapper.map(user, UserDTO.class);

	}

	@RequestMapping(value = "/{id}.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public UserDTO listasJson(@PathVariable("id") Long id) {
		User user = accountService.getUser(id);
		return BeanMapper.map(user, UserDTO.class);
	}
}
