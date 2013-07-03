package org.springside.examples.showcase.demos.hystrix.service;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.examples.showcase.webservice.rest.UserDTO;

/**
 * 使用Hystrix访问资源的服务。
 */
@Controller
public class ServiceController {
	@RequestMapping(value = "/hystrix/service/{id}", method = RequestMethod.GET)
	@ResponseBody
	public UserDTO getUser(@PathVariable("id") Long id) {
		GetUserCommand command = new GetUserCommand(id);
		return command.execute();
	}
}
