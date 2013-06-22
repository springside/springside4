package org.springside.examples.showcase.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BusinessLogger {
	// 业务日志的logger
	private static Logger businessLogger = LoggerFactory.getLogger("business");

	public void log(String action, String user, Map data) {
		businessLogger.info("{},{},{}", action, user, data);
	}
}
