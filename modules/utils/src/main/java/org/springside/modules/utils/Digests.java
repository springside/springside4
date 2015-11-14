/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;

import org.apache.commons.lang3.Validate;

import com.google.common.hash.Hashing;

/**
 * 消息摘要的工具类.
 * 
 * 支持SHA-1/MD5这些安全性较高，返回byte[]的(可用Encodes进一步被编码为Hex, Base64或UrlSafeBase64).
 * 
 * 也支持crc32，murmur32这些不追求安全性，性能较高，返回int的.
 * 
 * @author calvin
 */
public class Digests {

	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static final String SHA1 = "SHA-1";
	private static final String MD5 = "MD5";

	private static SecureRandom random = new SecureRandom();

	/**
	 * 对输入字符串进行sha1散列.
	 */
	public static byte[] sha1(byte[] input) {
		return digest(input, SHA1, null, 1);
	}

	public static byte[] sha1(String input) {
		return digest(input.getBytes(UTF8), SHA1, null, 1);
	}

	public static byte[] sha1(byte[] input, byte[] salt) {
		return digest(input, SHA1, salt, 1);
	}

	public static byte[] sha1(String input, byte[] salt) {
		return digest(input.getBytes(UTF8), SHA1, salt, 1);
	}

	public static byte[] sha1(byte[] input, byte[] salt, int iterations) {
		return digest(input, SHA1, salt, iterations);
	}

	public static byte[] sha1(String input, byte[] salt, int iterations) {
		return digest(input.getBytes(UTF8), SHA1, salt, iterations);
	}

	/**
	 * 对字符串进行散列, 支持md5与sha1算法.
	 */
	private static byte[] digest(byte[] input, String algorithm, byte[] salt, int iterations) {
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);

			if (salt != null) {
				digest.update(salt);
			}

			byte[] result = digest.digest(input);

			for (int i = 1; i < iterations; i++) {
				digest.reset();
				result = digest.digest(result);
			}
			return result;
		} catch (GeneralSecurityException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 生成随机的Byte[]作为salt.
	 * 
	 * @param numBytes salt数组的大小
	 */
	public static byte[] generateSalt(int numBytes) {
		Validate.isTrue(numBytes > 0, "numBytes argument must be a positive integer (1 or larger)", numBytes);

		byte[] bytes = new byte[numBytes];
		random.nextBytes(bytes);
		return bytes;
	}

	/**
	 * 对文件进行md5散列.
	 */
	public static byte[] md5(InputStream input) throws IOException {
		return digest(input, MD5);
	}

	/**
	 * 对文件进行sha1散列.
	 */
	public static byte[] sha1(InputStream input) throws IOException {
		return digest(input, SHA1);
	}

	private static byte[] digest(InputStream input, String algorithm) throws IOException {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			int bufferLength = 8 * 1024;
			byte[] buffer = new byte[bufferLength];
			int read = input.read(buffer, 0, bufferLength);

			while (read > -1) {
				messageDigest.update(buffer, 0, read);
				read = input.read(buffer, 0, bufferLength);
			}

			return messageDigest.digest();
		} catch (GeneralSecurityException e) {
			throw Exceptions.unchecked(e);
		}
	}

	public static int crc32(byte[] input) {
		return Hashing.crc32().hashBytes(input).asInt();
	}

	public static int crc32(String input) {
		return Hashing.crc32().hashString(input, UTF8).asInt();
	}

	public static int murmur32(byte[] input) {
		return Hashing.murmur3_32().hashBytes(input).asInt();
	}

	public static int murmur32(String input) {
		return Hashing.murmur3_32().hashString(input, UTF8).asInt();
	}

	public static int murmur32(byte[] input, int seed) {
		return Hashing.murmur3_32(seed).hashBytes(input).asInt();
	}

	public static int murmur32(String input, int seed) {
		return Hashing.murmur3_32(seed).hashString(input, UTF8).asInt();
	}
}
