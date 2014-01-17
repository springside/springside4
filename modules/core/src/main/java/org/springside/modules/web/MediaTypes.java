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

	public final static String APPLICATION_XML = "application/xml";
	public final static String APPLICATION_XML_UTF_8 = "application/xml; charset=UTF-8";

	public final static String JSON = "application/json";
	public final static String JSON_UTF_8 = "application/json; charset=UTF-8";

	public final static String JAVASCRIPT = "application/javascript";
	public final static String JAVASCRIPT_UTF_8 = "application/javascript; charset=UTF-8";

	public final static String APPLICATION_XHTML_XML = "application/xhtml+xml";
	public final static String APPLICATION_XHTML_XML_UTF_8 = "application/xhtml+xml; charset=UTF-8";

	public final static String TEXT_PLAIN = "text/plain";
	public final static String TEXT_PLAIN_UTF_8 = "text/plain; charset=UTF-8";

	public final static String TEXT_XML = "text/xml";
	public final static String TEXT_XML_UTF_8 = "text/xml; charset=UTF-8";

	public final static String TEXT_HTML = "text/html";
	public final static String TEXT_HTML_UTF_8 = "text/html; charset=UTF-8";
}
