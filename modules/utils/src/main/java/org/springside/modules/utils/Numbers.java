package org.springside.modules.utils;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

/**
 * 数字的工具类
 * 
 * 1.基于Guava的原始类型与byte[]的转换.
 *
 */
public class Numbers {

	public static byte[] longToBytes(long value) {
		return Longs.toByteArray(value);
	}

	public static long bytesToLong(byte[] bytes) {
		return Longs.fromByteArray(bytes);
	}

	public static byte[] intToBytes(int value) {
		return Ints.toByteArray(value);
	}

	public static int bytesToInt(byte[] bytes) {
		return Ints.fromByteArray(bytes);
	}
}
