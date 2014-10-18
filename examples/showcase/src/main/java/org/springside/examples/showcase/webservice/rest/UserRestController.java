/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.webservice.rest;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springside.examples.showcase.entity.User;
import org.springside.examples.showcase.service.AccountEffectiveService;
import org.springside.modules.mapper.BeanMapper;
import org.springside.modules.metrics.Timer;
import org.springside.modules.metrics.Timer.TimerContext;
import org.springside.modules.metrics.MetricRegistry;

/**
 * Shiro的配置文件中对/api/secure/**进行拦截，要求authBasic认证.
 * 
 * @author calvin
 */
@RestController
@RequestMapping(value = { "/api/v1/user", "/api/secure/v1/user" })
public class UserRestController {
	private static Logger logger = LoggerFactory.getLogger(UserRestController.class);

	@Autowired
	private AccountEffectiveService accountService;

	private Timer executionMetrics;

	@PostConstruct
	public void register() {
		executionMetrics = MetricRegistry.INSTANCE.timer("REST.GetUser");
	}

	/**
	 * 基于ContentNegotiationManager,根据URL的后缀渲染不同的格式
	 * eg. /api/v1/user/1.xml 返回xml
	 * /api/v1/user/1.json 返回json
	 * /api/v1/user/1 返回xml(why?)
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public UserDTO getUser(@PathVariable("id") Long id) {
		final TimerContext exectuionTimer = executionMetrics.start();
		try {
			User user = accountService.getUser(id);

			if (user == null) {
				String message = "用户不存在(id:" + id + ")";
				logger.warn(message);
				throw new RestException(HttpStatus.NOT_FOUND, message);
			}

			// 使用Dozer转换DTO类，并补充Dozer不能自动绑定的属性
			UserDTO dto = BeanMapper.map(user, UserDTO.class);
			dto.setTeamId(user.getTeam().getId());
			return dto;
		} finally {
			exectuionTimer.stop();
		}
	}
}
