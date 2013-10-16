package org.springside.examples.showcase.demos.hystrix.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springside.examples.showcase.webservice.rest.UserDTO;

import com.netflix.hystrix.HystrixCommand;

public class GetUserCommand extends HystrixCommand<UserDTO> {

	private static Logger logger = LoggerFactory.getLogger(GetUserCommand.class);

	private RestTemplate restTemplate;

	private Long id;

	protected GetUserCommand(Setter config, RestTemplate restTemplate, Long id) {
		super(config);

		this.restTemplate = restTemplate;
		this.id = id;
	}

	/**
	 * 实际访问依赖资源的函数的实现。
	 */
	@Override
	protected UserDTO run() throws Exception {
		logger.info("Access restful resource");
		return restTemplate.getForObject("http://localhost:8080/showcase/hystrix/resource/{id}", UserDTO.class, id);
	}
}
