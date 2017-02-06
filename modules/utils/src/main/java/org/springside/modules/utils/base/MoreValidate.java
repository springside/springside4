package org.springside.modules.utils.base;

import org.springside.modules.utils.base.annotation.Nullable;

/**
 * 参数校验统一使用Apache Common Lange Validate, 补充一些缺少的.
 * 
 * 为什么不用Guava的Preconditions? 无他，
 * 
 * 一是少打几个字而已， 二是Validate的方法多，比如noNullElements()判断多个元素都不为空
 * 
 * @see com.google.common.math.MathPreconditions
 * 
 * @author calvin
 */
public class MoreValidate {

	/**
	 * 校验为正数则返回该数字，否则抛出异常.
	 */
	public static int positive(@Nullable String role, int x) {
		if (x <= 0) {
			throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
		}
		return x;
	}

	/**
	 * 校验为正数则返回该数字，否则抛出异常.
	 */
	public static Integer positive(@Nullable String role, Integer x) {
		if (x.intValue() <= 0) {
			throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
		}
		return x;
	}

	/**
	 * 校验为正数则返回该数字，否则抛出异常.
	 */
	public static long positive(@Nullable String role, long x) {
		if (x <= 0) {
			throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
		}
		return x;
	}

	/**
	 * 校验为正数则返回该数字，否则抛出异常.
	 */
	public static Long positive(@Nullable String role, Long x) {
		if (x.longValue() <= 0) {
			throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
		}
		return x;
	}

	/**
	 * 校验为正数则返回该数字，否则抛出异常.
	 */
	public static double positive(@Nullable String role, double x) {
		if (!(x > 0)) { // not x < 0, to work with NaN.
			throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
		}
		return x;
	}

	/**
	 * 校验为正数则返回该数字，否则抛出异常.
	 */
	public static int nonNegative(@Nullable String role, int x) {
		if (x < 0) {
			throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
		}
		return x;
	}

	/**
	 * 校验为正数则返回该数字，否则抛出异常.
	 */
	public static Integer nonNegative(@Nullable String role, Integer x) {
		if (x.intValue() < 0) {
			throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
		}
		return x;
	}

	/**
	 * 校验为正数则返回该数字，否则抛出异常.
	 */
	public static long nonNegative(@Nullable String role, long x) {
		if (x < 0) {
			throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
		}
		return x;
	}

	/**
	 * 校验为正数则返回该数字，否则抛出异常.
	 */
	public static Long nonNegative(@Nullable String role, Long x) {
		if (x.longValue() < 0) {
			throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
		}
		return x;
	}

	/**
	 * 校验为正数则返回该数字，否则抛出异常.
	 */
	public static double nonNegative(@Nullable String role, double x) {
		if (!(x >= 0)) { // not x < 0, to work with NaN.
			throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
		}
		return x;
	}
}
