/*******************************************************************************
 * Copyright (c) 2005, 2017 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.utils.misc;

import java.security.SecureRandom;
import java.util.UUID;

import org.springside.modules.utils.number.RandomUtil;
import org.springside.modules.utils.text.EncodeUtil;

import com.google.common.annotations.Beta;

/**
 * 封装各种生成唯一性ID算法的工具类.
 */
@Beta
public class IdGenerator {

	private static SecureRandom random = new SecureRandom();

	/**
	 * 封装JDK自带的UUID, 通过Random数字生成, 中间有-分割.
	 */
	public static String uuid() {
		return UUID.randomUUID().toString();
	}

	/**
	 * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
	 */
	public static String uuid2() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * 使用SecureRandom随机生成Long.
	 */
	public static long randomLong() {
		return RandomUtil.nextLong();
	}

	/**
	 * 基于URLSafeBase64编码的SecureRandom随机生成bytes.
	 */
	public static String randomBase64(int length) {
		byte[] randomBytes = new byte[length];
		random.nextBytes(randomBytes);
		return EncodeUtil.encodeBase64UrlSafe(randomBytes);
	}
}
