package org.springside.examples.showcase.demos.hystrix.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.examples.showcase.demos.hystrix.service.UserService;
import org.springside.examples.showcase.webservice.rest.RestException;
import org.springside.examples.showcase.webservice.rest.UserDTO;

@Controller
public class HystrixDemoController {

	private static Logger logger = LoggerFactory.getLogger(HystrixDemoController.class);

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/hystrix/user/{id}", method = RequestMethod.GET)
	@ResponseBody
	public UserDTO getUser(@PathVariable("id") Long id) {

		try {
			return userService.getUser(id);
		} catch (Exception e) {
			// 化简异常处理，正式项目应区分错误类型.
			logger.error(e.getMessage(), e);
			throw new RestException();
		}
	}
}
