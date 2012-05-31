/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.springside.modules.test.log;

import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * 在List中保存日志的Appender, 用于测试Log4j的日志输出.
 * 
 * 在测试开始前, 使用任意一种addToLogger()方法将此appender添加到需要侦听的logger中.
 * 
 * @author calvin
 */
public class Log4jMockAppender extends AppenderSkeleton {

	private List<LoggingEvent> logs = Lists.newArrayList();

	/**
	 * 返回之前append的第一个log.
	 */
	public LoggingEvent getFirstLog() {
		if (logs.isEmpty()) {
			return null;
		}
		return logs.get(0);
	}

	/**
	 * 返回之前append的第一个log的信息.
	 */
	public String getFirstMessage() {
		if (logs.isEmpty()) {
			return null;
		}
		return getFirstLog().getMessage().toString();
	}

	/**
	 * 返回之前append的最后一个log.
	 */
	public LoggingEvent getLastLog() {
		if (logs.isEmpty()) {
			return null;
		}
		return Iterables.getLast(logs);
	}

	/**
	 * 返回之前append的最后一个log的信息.
	 */
	public String getLastMessage() {
		if (logs.isEmpty()) {
			return null;
		}
		return getLastLog().getMessage().toString();
	}

	/**
	 * 返回之前append的所有log.
	 */
	public List<LoggingEvent> getAllLogs() {
		return logs;
	}

	/**
	 * 返回Log的数量。
	 */
	public int getLogsCount() {
		return logs.size();
	}

	/**
	 * 判断是否有log.
	 */
	public boolean isEmpty() {
		return logs.isEmpty();
	}

	/**
	 * 清除之前append的所有log.
	 */
	public void clearLogs() {
		logs.clear();
	}

	/**
	 * 将此appender添加到logger中.
	 */
	public void addToLogger(String loggerName) {
		Logger logger = Logger.getLogger(loggerName);
		logger.addAppender(this);
	}

	/**
	 * 将此appender添加到logger中.
	 */
	public void addToLogger(Class<?> loggerClass) {
		Logger logger = Logger.getLogger(loggerClass);
		logger.addAppender(this);
	}

	/**
	 * 将此appender从logger中移除.
	 */
	public void removeFromLogger(String loggerName) {
		Logger logger = Logger.getLogger(loggerName);
		logger.removeAppender(this);
	}

	/**
	 * 将此appender从logger中移除.
	 */
	public void removeFromLogger(Class<?> loggerClass) {
		Logger logger = Logger.getLogger(loggerClass);
		logger.removeAppender(this);
	}

	/**
	 * 设置输出格式.
	 */
	public void setLayout(String pattern) {
		setLayout(new PatternLayout(pattern));
	}

	/**
	 * 实现AppenderSkeleton的append函数, 将log加入到内部的List.
	 */
	@Override
	protected void append(LoggingEvent event) {
		logs.add(event);
	}

	/**
	 * @see AppenderSkeleton#close()
	 */
	@Override
	public void close() {
		logs.clear();
	}

	/**
	 * @see AppenderSkeleton#requiresLayout()
	 */
	@Override
	public boolean requiresLayout() {
		return false;
	}
}
