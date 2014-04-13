/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springside.modules.mapper.JsonMapper;

/**
 * 打印业务日志，格式为:
 * 
 * 日期,实体类型,操作类型 ,操作用户,json格式的扩展字段
 * 
 * @author calvin
 */
@Component
public class BusinessLogger {
	public static final String BUSINESS_LOGGER_NAME = "business";

	private Logger businessLogger = LoggerFactory.getLogger(BUSINESS_LOGGER_NAME);
	private JsonMapper jsonMapper = new JsonMapper();

	public void log(String entity, String action, String user, Map data) {
		String json = (data != null ? jsonMapper.toJson(data) : "{}");
		businessLogger.info("{},{},{},{}", entity, action, user, json);
	}
}
