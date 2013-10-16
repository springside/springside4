package org.springside.examples.showcase.demos.hystrix.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springside.examples.showcase.webservice.rest.UserDTO;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.exception.HystrixBadRequestException;

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
		UserDTO user;

		try {
			logger.info("Access restful resource");
			user = restTemplate.getForObject("http://localhost:8080/showcase/hystrix/resource/{id}", UserDTO.class, id);
		} catch (HttpClientErrorException e) {
			// 排除 400错误不算入错误统计内
			HttpStatus status = e.getStatusCode();
			if (status.equals(HttpStatus.BAD_REQUEST)) {
				throw new HystrixBadRequestException(e.getResponseBodyAsString(), e);
			}
			throw e;
		}
		return user;
	}
}
