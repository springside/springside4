/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.springside.modules.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RsResponse {

	private static Logger logger = LoggerFactory.getLogger(RsResponse.class);

	/**
	 * 创建WebApplicationException并记打印日志, 使用标准状态码与自定义信息并记录错误信息.
	 */
	public static WebApplicationException buildException(Status status, String message) {
		return buildException(status.getStatusCode(), message);
	}

	/**
	 * 创建WebApplicationException并打印日志, 使用自定义状态码与自定义信息并记录错误信息.
	 */
	public static WebApplicationException buildException(int status, String message) {
		logger.error("Restful Service Error, Status " + status + ":" + message);
		return new WebApplicationException(buildTextResponse(status, message));
	}

	/**
	 * 创建状态码为500的默认WebApplicatonExcetpion, 并在日志中打印RuntimeExcetpion的信息.
	 * 如RuntimeException为WebApplicatonExcetpion则跳过不进行处理.
	 */
	public static WebApplicationException buildDefaultException(RuntimeException e) {
		if (e instanceof WebApplicationException) {
			return (WebApplicationException) e;
		} else {
			logger.error("Restful Service Error, Status 500:" + e.getMessage(), e);
			return new WebApplicationException();
		}
	}

	/**
	 * 创建类型为"text/plain"的Response。
	 */
	public static Response buildTextResponse(Status status, String message) {
		return buildTextResponse(status.getStatusCode(), message);
	}

	/**
	 * 创建类型为"text/plain"的Response。
	 */
	public static Response buildTextResponse(int status, String message) {
		return Response.status(status).entity(message).type(MediaType.TEXT_PLAIN).build();
	}

	/**
	 * 创建类型为"application/json"的Response。
	 */
	public static Response buildJsonResponse(Status status, Object content) {
		return buildJsonResponse(status.getStatusCode(), content);
	}

	/**
	 * 创建类型为"application/json"的Response。
	 */
	public static Response buildJsonResponse(int status, Object content) {
		return Response.status(status).entity(content).type(MediaType.APPLICATION_JSON).build();
	}

}
