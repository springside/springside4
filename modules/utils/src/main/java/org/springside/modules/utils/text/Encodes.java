/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.utils.text;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springside.modules.utils.base.Exceptions;

/**
 * 封装各种格式的编码解码工具类.
 * 
 * 1.Commons-Codec的 hex/base64 编解码 
 * 
 * 2.Commons-Lang的json/xml/html 编解码 
 * 
 * 3.JDK提供的URL 编解码
 */
public class Encodes {

	private static final String DEFAULT_URL_ENCODING = "UTF-8";

	/**
	 * Hex编码.
	 */
	public static String encodeHex(byte[] input) {
		return Hex.encodeHexString(input);
	}

	/**
	 * Hex解码.
	 */
	public static byte[] decodeHex(String input) {
		try {
			return Hex.decodeHex(input.toCharArray());
		} catch (DecoderException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * Base64编码.
	 */
	public static String encodeBase64(byte[] input) {
		return Base64.encodeBase64String(input);
	}

	/**
	 * Base64编码, URL安全(将Base64中的URL非法字符'+'和'/'转为'-'和'_', 见RFC3548).
	 */
	public static String encodeUrlSafeBase64(byte[] input) {
		return Base64.encodeBase64URLSafeString(input);
	}

	/**
	 * Base64解码.
	 */
	public static byte[] decodeBase64(String input) {
		return Base64.decodeBase64(input);
	}

	/**
	 * Json转码，将字符串转码为符合JSON格式的字符串.
	 * 
	 * 比如 "Stop!" 转化为\"Stop!\"
	 */
	public static String escapeJson(String json) {
		return StringEscapeUtils.escapeJson(json);
	}

	/**
	 * Xml转码，将字符串转码为符合XML1.1格式的字符串.
	 * 
	 * 比如 "bread" & "butter" 转化为 &quot;bread&quot; &amp; &quot;butter&quot;
	 */
	public static String escapeXml(String xml) {
		return StringEscapeUtils.escapeXml11(xml);
	}

	/**
	 * Xml转码，XML格式的字符串解码为普通字符串.
	 * 
	 * 比如 &quot;bread&quot; &amp; &quot;butter&quot; 转化为"bread" & "butter"
	 */
	public static String unescapeXml(String xml) {
		return StringEscapeUtils.unescapeXml(xml);
	}

	/**
	 * Html转码，将字符串转码为符合HTML4格式的字符串.
	 * 
	 * 比如 "bread" & "butter" 转化为 &quot;bread&quot; &amp; &quot;butter&quot;
	 */
	public static String escapeHtml(String html) {
		return StringEscapeUtils.escapeHtml4(html);
	}

	/**
	 * Html解码，将HTML4格式的字符串转码解码为普通字符串.
	 * 
	 * 比如 &quot;bread&quot; &amp; &quot;butter&quot;转化为"bread" & "butter"
	 */
	public static String unescapeHtml(String html) {
		return StringEscapeUtils.unescapeHtml4(html);
	}

	/**
	 * URL 编码, Encode默认为UTF-8.
	 */
	public static String urlEncode(String part) {
		try {
			return URLEncoder.encode(part, DEFAULT_URL_ENCODING);
		} catch (UnsupportedEncodingException ignored) {
			return null;
		}
	}

	/**
	 * URL 解码, Encode默认为UTF-8.
	 */
	public static String urlDecode(String part) {
		try {
			return URLDecoder.decode(part, DEFAULT_URL_ENCODING);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
}
