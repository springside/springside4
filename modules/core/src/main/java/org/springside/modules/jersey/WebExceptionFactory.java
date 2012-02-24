/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.springside.modules.jersey;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;

public class WebExceptionFactory {

	private WebExceptionFactory() {
	}

	/**
	 * 创建WebApplicationException并记打印日志, 使用标准状态码与自定义信息并记录错误信息.
	 */
	public static WebApplicationException buildException(Status status, String message, Logger logger) {
		logger.error(status.getStatusCode() + ":" + message);
		return new WebApplicationException(Response.status(status).entity(message).type(MediaType.TEXT_PLAIN).build());
	}

	/**
	 * 创建WebApplicationException并打印日志, 使用自定义状态码与自定义信息并记录错误信息.
	 */
	public static WebApplicationException buildException(int status, String message, Logger logger) {
		logger.error(status + ":" + message);
		return new WebApplicationException(Response.status(status).entity(message).type(MediaType.TEXT_PLAIN).build());
	}

	/**
	 * 创建状态码为500的默认WebApplicatonExcetpion, 并在日志中打印RuntimeExcetpion的信息.
	 * 如RuntimeException为WebApplicatonExcetpion则跳过不进行处理.
	 */
	public static WebApplicationException buildDefaultException(RuntimeException e, Logger logger) {
		if (e instanceof WebApplicationException) {
			return (WebApplicationException) e;
		} else {
			logger.error("500:" + e.getMessage(), e);
			return new WebApplicationException();
		}
	}
}
