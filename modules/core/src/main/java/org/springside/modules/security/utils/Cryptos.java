/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.springside.modules.security.utils;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springside.modules.utils.Exceptions;

/**
 * 支持HMAC-SHA1消息签名 及 DES/AES对称加密的工具类.
 * 
 * 支持Hex与Base64两种编码方式.
 * 
 * @author calvin
 */
public class Cryptos {

	private static final String DES = "DES";
	private static final String AES = "AES";
	private static final String HMACSHA1 = "HmacSHA1";

	private static final int DEFAULT_HMACSHA1_KEYSIZE = 160;//RFC2401
	private static final int DEFAULT_AES_KEYSIZE = 128;

	private Cryptos() {
	}

	//-- HMAC-SHA1 funciton --//
	/**
	 * 使用HMAC-SHA1进行消息签名, 返回字节数组,长度为20字节.
	 * 
	 * @param input 原始输入字符串
	 * @param keyBytes HMAC-SHA1密钥
	 */
	public static byte[] hmacSha1(String input, byte[] keyBytes) {
		try {
			SecretKey secretKey = new SecretKeySpec(keyBytes, HMACSHA1);
			Mac mac = Mac.getInstance(HMACSHA1);
			mac.init(secretKey);
			return mac.doFinal(input.getBytes());
		} catch (GeneralSecurityException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 校验HMAC-SHA1签名是否正确.
	 * 
	 * @param expected 已存在的签名
	 * @param input 原始输入字符串
	 * @param keyBytes 密钥
	 */
	public static boolean isMacValid(byte[] expected, String input, byte[] keyBytes) {
		byte[] actual = hmacSha1(input, keyBytes);
		return Arrays.equals(expected, actual);
	}

	/**
	 * 生成HMAC-SHA1密钥,返回字节数组,长度为160位(20字节).
	 * HMAC-SHA1算法对密钥无特殊要求, RFC2401建议最少长度为160位(20字节).
	 */
	public static byte[] generateMacSha1Key() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(HMACSHA1);
			keyGenerator.init(DEFAULT_HMACSHA1_KEYSIZE);
			SecretKey secretKey = keyGenerator.generateKey();
			return secretKey.getEncoded();
		} catch (GeneralSecurityException e) {
			throw Exceptions.unchecked(e);
		}
	}

	//-- DES function --//
	/**
	 * 使用DES加密原始字符串.
	 * 
	 * @param input 原始输入字符串
	 * @param keyBytes 符合DES要求的密钥
	 */
	public static byte[] desEncrypt(String input, byte[] keyBytes) {
		return des(input.getBytes(), keyBytes, Cipher.ENCRYPT_MODE);
	}

	/**
	 * 使用DES解密加密的字符串, 返回原始字符串.
	 * 
	 * @param input Hex编码的加密字符串
	 * @param keyBytes 符合DES要求的密钥
	 */
	public static String desDecrypt(byte[] input, byte[] keyBytes) {
		byte[] decryptResult = des(input, keyBytes, Cipher.DECRYPT_MODE);
		return new String(decryptResult);
	}

	/**
	 * 使用DES加密或解密无编码的原始字节数组, 返回无编码的字节数组结果.
	 * 
	 * @param inputBytes 原始字节数组
	 * @param keyBytes 符合DES要求的密钥
	 * @param mode Cipher.ENCRYPT_MODE 或 Cipher.DECRYPT_MODE
	 */
	private static byte[] des(byte[] inputBytes, byte[] keyBytes, int mode) {
		try {
			DESKeySpec desKeySpec = new DESKeySpec(keyBytes);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

			Cipher cipher = Cipher.getInstance(DES);
			cipher.init(mode, secretKey);
			return cipher.doFinal(inputBytes);
		} catch (GeneralSecurityException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 生成符合DES要求的密钥, 长度为64位(8字节).
	 */
	public static byte[] generateDesKey() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(DES);
			SecretKey secretKey = keyGenerator.generateKey();
			return secretKey.getEncoded();
		} catch (GeneralSecurityException e) {
			throw Exceptions.unchecked(e);
		}
	}

	//-- AES funciton --//
	/**
	 * 使用AES加密原始字符串.
	 * 
	 * @param input 原始输入字符串
	 * @param keyBytes 符合AES要求的密钥
	 */
	public static byte[] aesEncrypt(String input, byte[] keyBytes) {
		return aes(input.getBytes(), keyBytes, Cipher.ENCRYPT_MODE);
	}

	/**
	 * 使用AES解密字符串, 返回原始字符串.
	 * 
	 * @param input Hex编码的加密字符串
	 * @param keyBytes 符合AES要求的密钥
	 */
	public static String aesDecrypt(byte[] input, byte[] keyBytes) {
		byte[] decryptResult = aes(input, keyBytes, Cipher.DECRYPT_MODE);
		return new String(decryptResult);
	}

	/**
	 * 使用AES加密或解密无编码的原始字节数组, 返回无编码的字节数组结果.
	 * 
	 * @param inputBytes 原始字节数组
	 * @param keyBytes 符合AES要求的密钥
	 * @param mode Cipher.ENCRYPT_MODE 或 Cipher.DECRYPT_MODE
	 */
	private static byte[] aes(byte[] inputBytes, byte[] keyBytes, int mode) {
		try {
			SecretKey secretKey = new SecretKeySpec(keyBytes, AES);
			Cipher cipher = Cipher.getInstance(AES);
			cipher.init(mode, secretKey);
			return cipher.doFinal(inputBytes);
		} catch (GeneralSecurityException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 生成AES密钥,返回字节数组,长度为128位(16字节).
	 */
	public static byte[] generateAesKey() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
			keyGenerator.init(DEFAULT_AES_KEYSIZE);
			SecretKey secretKey = keyGenerator.generateKey();
			return secretKey.getEncoded();
		} catch (GeneralSecurityException e) {
			throw Exceptions.unchecked(e);
		}
	}
}