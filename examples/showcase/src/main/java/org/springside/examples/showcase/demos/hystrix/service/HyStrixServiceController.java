package org.springside.examples.showcase.demos.hystrix.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.examples.showcase.webservice.rest.RestException;
import org.springside.examples.showcase.webservice.rest.UserDTO;

/**
 * 使用Hystrix访问资源的服务。
 */
@Controller
public class HyStrixServiceController {
	private static Logger logger = LoggerFactory.getLogger(HyStrixServiceController.class);

	@RequestMapping(value = "/hystrix/service/{id}", method = RequestMethod.GET)
	@ResponseBody
	public UserDTO getUser(@PathVariable("id") Long id) {
		GetUserCommand command = new GetUserCommand(id);

		try {
			return command.execute();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RestException(HttpStatus.SERVICE_UNAVAILABLE);
		}
	}
}
