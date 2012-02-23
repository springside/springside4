package org.springside.modules.log;

import static org.junit.Assert.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springside.modules.log.Log4jMBean;
import org.springside.modules.log.MockLog4jAppender;

public class Log4jMBeanTest {

	@Test
	public void testLoggerLevel() {

		String loggerName = "org.springside.modules";
		Log4jMBean mbean = new Log4jMBean();
		String orgLevel = mbean.getLoggerLevel(loggerName);

		Logger.getLogger(loggerName).setLevel(Level.FATAL);
		assertEquals("FATAL", mbean.getLoggerLevel(loggerName));

		mbean.setLoggerLevel(loggerName, "TRACE");
		assertEquals("TRACE", mbean.getLoggerLevel(loggerName));

		mbean.setLoggerLevel(loggerName, "WRONG_LEVEL_NAME");
		assertEquals("DEBUG", mbean.getLoggerLevel(loggerName));

		mbean.setLoggerLevel(loggerName, orgLevel);
	}

	@Test
	public void startAndStopTrace() {
		//准备Logger
		String loggerName = "org.springside.examples";
		String traceAppenderName = "TraceFile";

		org.slf4j.Logger logger = LoggerFactory.getLogger(loggerName);

		Log4jMBean mbean = new Log4jMBean();
		mbean.setProjectLoggerName(loggerName);
		mbean.setTraceAppenderName(traceAppenderName);

		MockLog4jAppender normalAppender = new MockLog4jAppender();
		normalAppender.setName("RollingFile");
		normalAppender.setThreshold(Level.INFO);
		normalAppender.addToLogger(loggerName);

		MockLog4jAppender traceAppender = new MockLog4jAppender();
		traceAppender.setName(traceAppenderName);
		traceAppender.setThreshold(Level.OFF);
		traceAppender.addToLogger(loggerName);

		mbean.setLoggerLevel(loggerName, "INFO");

		//未开始Trace, info级别信息只写到normal appender
		//debug信息不会写到任何appender
		logger.info("before trace");
		assertEquals("before trace", normalAppender.getFirstMessage());
		assertTrue(traceAppender.isEmpty());
		normalAppender.clearLogs();
		traceAppender.clearLogs();

		logger.debug("before trace");
		assertTrue(normalAppender.isEmpty());
		assertTrue(traceAppender.isEmpty());

		//开始trace, info级别信息写到所有appender
		//debug信息只写到trace appender
		mbean.startTrace();
		logger.info("start trace");
		assertEquals("start trace", normalAppender.getFirstMessage());
		assertEquals("start trace", traceAppender.getFirstMessage());
		normalAppender.clearLogs();
		traceAppender.clearLogs();

		logger.debug("start trace");
		assertTrue(normalAppender.isEmpty());
		assertEquals("start trace", traceAppender.getFirstMessage());
		normalAppender.clearLogs();
		traceAppender.clearLogs();

		//结束trace,逻辑与未开始Trace一样
		mbean.stopTrace();
		logger.info("after trace");
		assertEquals("after trace", normalAppender.getFirstMessage());
		assertTrue(traceAppender.isEmpty());
		normalAppender.clearLogs();
		traceAppender.clearLogs();

		logger.debug("after trace");
		assertTrue(normalAppender.isEmpty());
		assertTrue(traceAppender.isEmpty());
	}
}
