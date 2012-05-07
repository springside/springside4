package org.springside.modules.test.log;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log4jMockAppenderTest {

	@Test
	public void normal() {
		String testString1 = "Hello";
		String testString2 = "World";
		Log4jMockAppender appender = new Log4jMockAppender();
		appender.addToLogger(Log4jMockAppenderTest.class);

		//null
		assertNull(appender.getFirstLog());
		assertNull(appender.getLastLog());
		assertNull(appender.getFirstMessage());
		assertNull(appender.getFirstMessage());

		Logger logger = LoggerFactory.getLogger(Log4jMockAppenderTest.class);
		logger.warn(testString1);
		logger.warn(testString2);

		//getFirstLog/getLastLog
		assertEquals(testString1, appender.getFirstLog().getMessage());
		assertEquals(testString2, appender.getLastLog().getMessage());

		assertEquals(testString1, appender.getFirstMessage());
		assertEquals(testString2, appender.getLastMessage());

		//getAllLogs
		assertEquals(2, appender.getLogsCount());
		assertEquals(2, appender.getAllLogs().size());
		assertEquals(testString2, appender.getAllLogs().get(1).getMessage());

		//clearLogs
		appender.clearLogs();
		assertNull(appender.getFirstLog());
		assertNull(appender.getLastLog());
	}

	@Test
	public void addAndRemoveAppender() {
		String testString = "Hello";
		Logger logger = LoggerFactory.getLogger(Log4jMockAppenderTest.class);
		Log4jMockAppender appender = new Log4jMockAppender();
		//class
		appender.addToLogger(Log4jMockAppenderTest.class);
		logger.warn(testString);
		assertNotNull(appender.getFirstLog());

		appender.clearLogs();
		appender.removeFromLogger(Log4jMockAppenderTest.class);
		logger.warn(testString);
		assertNull(appender.getFirstLog());

		//name
		appender.clearLogs();
		appender.addToLogger("org.springside.modules.test.log");
		logger.warn(testString);
		assertNotNull(appender.getFirstLog());

		appender.clearLogs();
		appender.removeFromLogger("org.springside.modules.test.log");
		logger.warn(testString);
		assertNull(appender.getFirstLog());

	}
}
