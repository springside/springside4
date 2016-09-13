/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.utils;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class DigestsTest {

	@Test
	public void digestString() {
		String input = "user";
		byte[] sha1Result = Digests.sha1(input.getBytes());
		System.out.println("sha1 in hex result                               :" + Encodes.encodeHex(sha1Result));

		byte[] salt = Digests.generateSalt(8);
		System.out.println("salt in hex                                      :" + Encodes.encodeHex(salt));
		sha1Result = Digests.sha1(input.getBytes(), salt);
		System.out.println("sha1 in hex result with salt                     :" + Encodes.encodeHex(sha1Result));

		sha1Result = Digests.sha1(input.getBytes(), salt, 1024);
		System.out.println("sha1 in hex result with salt and 1024 interations:" + Encodes.encodeHex(sha1Result));

	}

	@Test
	public void digestFile() throws IOException {

		InputStream is = this.getClass().getClassLoader().getResourceAsStream("test.txt");
		byte[] md5result = Digests.md5(is);
		byte[] sha1result = Digests.sha1(is);
		System.out.println("md5: " + Encodes.encodeHex(md5result));
		System.out.println("sha1:" + Encodes.encodeHex(sha1result));
	}

	@Test
	public void crc32String() {

		String input = "user1";
		int result = Digests.crc32(input);
		System.out.println("crc32 for user1:" + result);

		input = "user2";
		result = Digests.crc32(input);
		System.out.println("crc32 for user2:" + result);
	}

	@Test
	public void murmurString() {

		String input1 = "user1";
		int result = Digests.murmur32(input1);
		System.out.println("murmur32 for user1:" + result);

		String input2 = "user2";
		result = Digests.murmur32(input2);
		System.out.println("murmur32 for user2:" + result);

		int seed = (int) System.currentTimeMillis();
		result = Digests.murmur32(input1, seed);
		System.out.println("murmur32 with seed for user1:" + result);

		result = Digests.murmur32(input2, seed);
		System.out.println("murmur32 with seed for user2:" + result);

	}
}
