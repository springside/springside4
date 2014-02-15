/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.web;

/**
 * 带UTF-8 charset 定义的MediaType.
 * 
 * Jax-RS和Spring的MediaType没有UTF-8的版本，
 * Google的MediaType必须再调用toString()函数而不是常量，不能用于Restful方法的annotation。
 * 
 * @author calvin
 */
public class MediaTypes {

	public static final String APPLICATION_XML = "application/xml";
	public static final String APPLICATION_XML_UTF_8 = "application/xml; charset=UTF-8";

	public static final String JSON = "application/json";
	public static final String JSON_UTF_8 = "application/json; charset=UTF-8";

	public static final String JAVASCRIPT = "application/javascript";
	public static final String JAVASCRIPT_UTF_8 = "application/javascript; charset=UTF-8";

	public static final String APPLICATION_XHTML_XML = "application/xhtml+xml";
	public static final String APPLICATION_XHTML_XML_UTF_8 = "application/xhtml+xml; charset=UTF-8";

	public static final String TEXT_PLAIN = "text/plain";
	public static final String TEXT_PLAIN_UTF_8 = "text/plain; charset=UTF-8";

	public static final String TEXT_XML = "text/xml";
	public static final String TEXT_XML_UTF_8 = "text/xml; charset=UTF-8";

	public static final String TEXT_HTML = "text/html";
	public static final String TEXT_HTML_UTF_8 = "text/html; charset=UTF-8";
}
