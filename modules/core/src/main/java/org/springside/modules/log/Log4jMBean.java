/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.springside.modules.log;

import java.util.Enumeration;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * 基于JMX动态配置Log4J日志级别，并控制Trace开关的MBean.
 * 
 * @author calvin
 */
@ManagedResource(objectName = Log4jMBean.LOG4J_MBEAN_NAME, description = "Log4j Management Bean")
public class Log4jMBean {

	/**
	 * Log4jMbean的注册名称.
	 */
	public static final String LOG4J_MBEAN_NAME = "Log4j:name=log4j";

	private static org.slf4j.Logger mbeanLogger = LoggerFactory.getLogger(Log4jMBean.class);

	private String projectLoggerName;

	private String traceAppenderName;

	private Level projectLoggerOrgLevel;

	/**
	 * 获取Logger的日志级别.
	 */
	@ManagedOperation(description = "Get logging level of the logger")
	@ManagedOperationParameters({ @ManagedOperationParameter(name = "loggerName", description = "Logger name") })
	public String getLoggerLevel(String loggerName) {
		Logger logger = Logger.getLogger(loggerName);
		return logger.getEffectiveLevel().toString();
	}

	/**
	 * 设置Logger的日志级别.
	 * 如果日志级别名称错误, 设为DEBUG.
	 */
	@ManagedOperation(description = "Set new logging level to the logger")
	@ManagedOperationParameters({ @ManagedOperationParameter(name = "loggerName", description = "Logger name"),
			@ManagedOperationParameter(name = "newlevel", description = "New level") })
	public void setLoggerLevel(String loggerName, String newLevel) {
		Logger logger = Logger.getLogger(loggerName);
		Level level = Level.toLevel(newLevel);
		logger.setLevel(level);
		mbeanLogger.info("设置{}级别为{}", loggerName, newLevel);
	}

	/**
	 * 获得项目默认logger的级别.
	 * 项目默认logger名称通过#setProjectLoggerName(String)配置.
	 */
	@ManagedAttribute(description = "Project default logging level of the logger")
	public String getProjectLoggerLevel() {
		return getLoggerLevel(projectLoggerName);
	}

	/**
	 * 设置项目默认logger的级别.
	 * 项目默认logger名称通过#setProjectLoggerName(String)配置.
	 */
	@ManagedAttribute(description = "Project default logging level of the logger")
	public void setProjectLoggerLevel(String newLevel) {
		setLoggerLevel(projectLoggerName, newLevel);
	}

	/**
	 * 开始Trace.
	 * 降低项目默认Logger的级别到DEBUG, 同时打开traceAppender的阀值到Debug.
	 * 需要先注入项目默认Logger名称及traceAppender名称.
	 */
	@ManagedOperation(description = "Start trace")
	public void startTrace() {
		Validate.notBlank(traceAppenderName);
		Logger logger = Logger.getLogger(projectLoggerName);
		projectLoggerOrgLevel = logger.getLevel();
		logger.setLevel(Level.DEBUG);
		setTraceAppenderThreshold(logger, Level.DEBUG);
		mbeanLogger.info("Start trace");
	}

	/**
	 * 结束Trace.
	 * 提升项目默认Logger的级别回到原来的值, 同时关闭traceAppender的阀值到Off.
	 * 需要先注入项目默认Logger名称及traceAppender名称.
	 */
	@ManagedOperation(description = "Stop trace")
	public void stopTrace() {
		Validate.notBlank(traceAppenderName);
		Logger logger = Logger.getLogger(projectLoggerName);
		logger.setLevel(projectLoggerOrgLevel);
		setTraceAppenderThreshold(logger, Level.OFF);
		mbeanLogger.info("Stop trace");
	}

	private void setTraceAppenderThreshold(Logger logger, Level level) {
		Enumeration e = logger.getAllAppenders();
		while (e.hasMoreElements()) {
			AppenderSkeleton appender = (AppenderSkeleton) e.nextElement();
			if (appender.getName().equals(traceAppenderName)) {
				appender.setThreshold(level);
			}
		}
	}

	/**
	 * 根据log4j.properties中的定义, 设置项目默认的logger名称, 如org.springside.examples.miniweb.
	 */
	public void setProjectLoggerName(String projectLoggerName) {
		this.projectLoggerName = projectLoggerName;
	}

	/**
	 * 根据log4j.properties中的定义,设置项目中的TraceAppender的名称
	 */
	public void setTraceAppenderName(String traceAppenderName) {
		this.traceAppenderName = traceAppenderName;
	}
}