/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.demos.hystrix.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springside.examples.showcase.webservice.rest.UserDTO;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.exception.HystrixBadRequestException;

/**
 * HystrixCommand的实现类, 将访问远程资源的逻辑封装在T run()函数内.
 * 如需对异常, 超时, 短路等进行自行处理，返回默认值则实现T getFallback()函数.
 * 
 * @author calvin
 */
public class GetUserCommand extends HystrixCommand<UserDTO> {

	private static Logger logger = LoggerFactory.getLogger(GetUserCommand.class);

	private RestTemplate restTemplate;
	private Long id;

	/**
	 * 构造函数，注入配置、用到的资源访问类和命令参数.
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
		} catch (HttpStatusCodeException e) {
			throw handleException(e);
		}
	}

	/**
	 * 处理异常，对于客户端自己的异常，抛出HystrixBadRequestException，不算入短路统计内。
	 */
	protected Exception handleException(HttpStatusCodeException e) {
		HttpStatus status = e.getStatusCode();
		if (status.equals(HttpStatus.BAD_REQUEST)) {
			throw new HystrixBadRequestException(e.getResponseBodyAsString(), e);
		}
		throw e;
	}
}
