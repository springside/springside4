/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.webservice.soap.response.base;

import javax.xml.bind.annotation.XmlType;

import org.springside.examples.showcase.webservice.soap.WsConstants;

/**
 * WebService返回结果基类,定义所有返回码.
 * 
 * @author calvin
 */
@XmlType(name = "WSResult", namespace = WsConstants.NS)
public class WSResult {

	// -- 返回代码定义 --//
	// 按项目的规则进行定义, 比如4xx代表客户端参数错误，5xx代表服务端业务错误等.
	public static final String SUCESS = "0";
	public static final String PARAMETER_ERROR = "400";

	public static final String SYSTEM_ERROR = "500";
	public static final String SYSTEM_ERROR_MESSAGE = "Runtime unknown internal error.";

	// -- WSResult基本属性 --//
	private String code = SUCESS;
	private String message;

	/**
	 * 创建结果.
	 */
	public void setError(String resultCode, String resultMessage) {
		code = resultCode;
		message = resultMessage;
	}

	/**
	 * 创建默认异常结果.
	 */
	public void setDefaultError() {
		setError(SYSTEM_ERROR, SYSTEM_ERROR_MESSAGE);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
