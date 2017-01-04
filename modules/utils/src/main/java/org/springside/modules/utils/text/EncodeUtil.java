/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.utils.text;

import com.google.common.io.BaseEncoding;

/**
 * hex/base64 编解码工具集，依赖Guava, 取消了对Commmon Codec的依赖
 * 
 * JDK8也有内置的BASE64类.
 */
public class EncodeUtil {

	/**
	 * Hex编码, 默认为abcdef为大写字母.
	 */
	public static String encodeHex(byte[] input) {
		return BaseEncoding.base16().encode(input);
	}

	/**
	 * Hex解码, 字符串有异常时抛出IllegalArgumentException.
	 */
	public static byte[] decodeHex(String input) {
		return BaseEncoding.base16().decode(input);
	}

	/**
	 * Base64编码.
	 */
	public static String encodeBase64(byte[] input) {
		return BaseEncoding.base64().encode(input);
	}

	/**
	 * Base64编码, URL安全(将Base64中的URL非法字符'+'和'/'转为'-'和'_', 见RFC3548).
	 */
	public static String encodeBase64UrlSafe(byte[] input) {
		return BaseEncoding.base64Url().encode(input);
	}

	/**
	 * Base64解码.
	 */
	public static byte[] decodeBase64(String input) {
		return BaseEncoding.base64().decode(input);
	}
	
	/**
	 * Base64解码, URL安全(将Base64中的URL非法字符'+'和'/'转为'-'和'_', 见RFC3548)..
	 */
	public static byte[] decodeBase64UrlSafe(String input) {
		return BaseEncoding.base64Url().decode(input);
	}
}
