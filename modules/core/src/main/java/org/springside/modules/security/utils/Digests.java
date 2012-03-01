/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.springside.modules.security.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

import org.springside.modules.utils.Encodes;
import org.springside.modules.utils.Exceptions;

/**
 * 支持SHA-1/MD5消息摘要的工具类.
 * 
 * 支持Hex与Base64两种编码方式.
 * 
 * @author calvin
 */
public class Digests {

	private static final String SHA1 = "SHA-1";
	private static final String MD5 = "MD5";

	private Digests() {
	}

	//-- String Hash function --//
	/**
	 * 对输入字符串进行sha1散列, 返回Hex编码的结果.
	 */
	public static String sha1Hex(String input) {
		byte[] digestResult = digest(input, SHA1);
		return Encodes.encodeHex(digestResult);
	}

	/**
	 * 对输入字符串进行sha1散列, 返回Base64编码的结果.
	 */
	public static String sha1Base64(String input) {
		byte[] digestResult = digest(input, SHA1);
		return Encodes.encodeBase64(digestResult);
	}

	/**
	 * 对输入字符串进行sha1散列, 返回Base64编码的URL安全的结果.
	 */
	public static String sha1Base64UrlSafe(String input) {
		byte[] digestResult = digest(input, SHA1);
		return Encodes.encodeUrlSafeBase64(digestResult);
	}

	/**
	 * 对字符串进行散列, 支持md5与sha1算法.
	 */
	private static byte[] digest(String input, String algorithm) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			return messageDigest.digest(input.getBytes());
		} catch (GeneralSecurityException e) {
			throw Exceptions.unchecked(e);
		}
	}

	//-- File Hash function --//
	/**
	 * 对文件进行md5散列, 返回Hex编码结果.
	 */
	public static String md5Hex(InputStream input) throws IOException {
		return digest(input, MD5);
	}

	/**
	 * 对文件进行sha1散列, 返回Hex编码结果.
	 */
	public static String sha1Hex(InputStream input) throws IOException {
		return digest(input, SHA1);
	}

	private static String digest(InputStream input, String algorithm) throws IOException {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			int bufferLength = 1024;
			byte[] buffer = new byte[bufferLength];
			int read = input.read(buffer, 0, bufferLength);

			while (read > -1) {
				messageDigest.update(buffer, 0, read);
				read = input.read(buffer, 0, bufferLength);
			}

			return Encodes.encodeHex(messageDigest.digest());

		} catch (GeneralSecurityException e) {
			throw Exceptions.unchecked(e);
		}
	}

}
