package org.springside.examples.showcase.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springside.modules.mapper.JsonMapper;

@Component
public class BusinessLogger {
	public static String BUSINESS_LOGGER = "business";

	private JsonMapper jsonMapper = new JsonMapper();
	// 业务日志的logger
	private static Logger businessLogger = LoggerFactory.getLogger(BUSINESS_LOGGER);

	public void log(String entity, String action, String user, Map data) {

		businessLogger.info("{},{},{},{}", entity, action, user, jsonMapper.toJson(data));
	}
}
