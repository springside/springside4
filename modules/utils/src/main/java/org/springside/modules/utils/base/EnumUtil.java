package org.springside.modules.utils.base;

import java.util.EnumSet;

import org.apache.commons.lang3.EnumUtils;

/**
 * 枚举工具集
 * 
 * 1. 将多个枚举值按bit保存到long中
 */
public class EnumUtil {

	/**
	 * 将若干个枚举值转换为long(按bits 1,2,4,8...的方式叠加)，用于使用long保存多个选项的情况.
	 */
	public static <E extends Enum<E>> long generateBits(final Class<E> enumClass, final Iterable<? extends E> values) {
		return EnumUtils.generateBitVector(enumClass, values);
	}

	/**
	 * 将若干个枚举值转换为long(按bits 1,2,4,8...的方式叠加)，用于使用long保存多个选项的情况.
	 */
	public static <E extends Enum<E>> long generateBits(final Class<E> enumClass, final E... values) {
		return EnumUtils.generateBitVector(enumClass, values);
	}

	/**
	 * long重新解析为若干个枚举值，用于使用long保存多个选项的情况.
	 */
	public static <E extends Enum<E>> EnumSet<E> processBits(final Class<E> enumClass, final long value) { // NOSONAR
		return EnumUtils.processBitVector(enumClass, value);
	}
}
