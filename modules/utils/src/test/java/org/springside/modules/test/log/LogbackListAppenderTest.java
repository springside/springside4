/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.test.log;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackListAppenderTest {

	@Test
	public void normal() {
		String testString1 = "Hello";
		String testString2 = "World";
		LogbackListAppender appender = new LogbackListAppender();
		appender.addToLogger(LogbackListAppenderTest.class);

		// null
		assertThat(appender.getFirstLog()).isNull();
		assertThat(appender.getLastLog()).isNull();
		assertThat(appender.getFirstMessage()).isNull();
		assertThat(appender.getFirstMessage()).isNull();

		Logger logger = LoggerFactory.getLogger(LogbackListAppenderTest.class);
		logger.warn(testString1);
		logger.warn(testString2);

		// getFirstLog/getLastLog
		assertThat(appender.getFirstLog().getMessage()).isEqualTo(testString1);
		assertThat(appender.getLastLog().getMessage()).isEqualTo(testString2);

		assertThat(appender.getFirstMessage()).isEqualTo(testString1);
		assertThat(appender.getLastMessage()).isEqualTo(testString2);

		// getAllLogs
		assertThat(appender.getLogsCount()).isEqualTo(2);
		assertThat(appender.getAllLogs()).hasSize(2);
		assertThat(appender.getAllLogs().get(1).getMessage()).isEqualTo(testString2);

		// clearLogs
		appender.clearLogs();
		assertThat(appender.getFirstLog()).isNull();
		assertThat(appender.getLastLog()).isNull();
	}

	@Test
	public void addAndRemoveAppender() {
		String testString = "Hello";
		Logger logger = LoggerFactory.getLogger(LogbackListAppenderTest.class);
		LogbackListAppender appender = new LogbackListAppender();
		// class
		appender.addToLogger(LogbackListAppenderTest.class);
		logger.warn(testString);
		assertThat(appender.getFirstLog()).isNotNull();

		appender.clearLogs();
		appender.removeFromLogger(LogbackListAppenderTest.class);
		logger.warn(testString);
		assertThat(appender.getFirstLog()).isNull();

		// name
		appender.clearLogs();
		appender.addToLogger("org.springside.modules.test.log");
		logger.warn(testString);
		assertThat(appender.getFirstLog()).isNotNull();

		appender.clearLogs();
		appender.removeFromLogger("org.springside.modules.test.log");
		logger.warn(testString);
		assertThat(appender.getFirstLog()).isNull();
	}
}
