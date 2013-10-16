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

	/**
	 * 构造函数，注入配置，命令用到的资源访问类和命令参数.
	 */
	protected GetUserCommand(Setter commandConfig, RestTemplate restTemplate, Long id) {
		super(commandConfig);
		this.restTemplate = restTemplate;
		this.id = id;
	}

	/**
	 * 访问依赖资源的函数的实现。
	 */
	@Override
	protected UserDTO run() throws Exception {
		try {
			logger.info("Access restful resource");
			return restTemplate.getForObject("http://localhost:8080/showcase/hystrix/resource/{id}", UserDTO.class, id);
		} catch (HttpClientErrorException e) {
			throw handleException(e);
		}
	}

	/**
	 * 处理异常，对于客户端本身的异常，抛出HystrixBadRequestException，不计算入短路统计内。
	 */
	protected Exception handleException(HttpClientErrorException e) {
		HttpStatus status = e.getStatusCode();
		if (status.equals(HttpStatus.BAD_REQUEST)) {
			throw new HystrixBadRequestException(e.getResponseBodyAsString(), e);
		}
		throw e;
	}
}
