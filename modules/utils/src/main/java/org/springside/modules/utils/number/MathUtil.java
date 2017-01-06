package org.springside.modules.utils.number;

import com.google.common.annotations.Beta;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

@Beta
public class MathUtil {

	/**
	 * 往上找出最接近的2的倍数，比如15返回16， 17返回32.
	 * 
	 * value必须为正数.
	 */
	public static int nextPowerOfTwo(int value) {
		return IntMath.ceilingPowerOfTwo(value);
	}

	/**
	 * 往上找出最接近的2的倍数，比如15返回16， 17返回32.
	 * 
	 * value必须为正数.
	 */
	public static long nextPowerOfTwo(long value) {
		return LongMath.ceilingPowerOfTwo(value);
	}

	/**
	 * 往下找出最接近2的倍数，比如15返回8， 17返回16.
	 * 
	 * value必须为正数.
	 */
	public static int previousPowerOfTwo(int value) {
		return IntMath.floorPowerOfTwo(value);
	}

	/**
	 * 往下找出最接近2的倍数，比如15返回8， 17返回16.
	 * 
	 * value必须为正数.
	 */
	public static long previousPowerOfTwo(long value) {
		return LongMath.floorPowerOfTwo(value);
	}

	/**
	 * 是否2的倍数
	 */
	public static boolean isPowerOfTwo(int value) {
		return IntMath.isPowerOfTwo(value);
	}

	/**
	 * 是否2的倍数
	 */
	public static boolean isPowerOfTwo(long value) {
		return LongMath.isPowerOfTwo(value);
	}

	/**
	 * 两个数的最大公约数，必须均为非负数
	 */
	public static int gcd(int a, int b) {
		return IntMath.gcd(a, b);
	}

	/**
	 * 两个数的最大公约数，必须均为非负数
	 */
	public static long gcd(long a, long b) {
		return LongMath.gcd(a, b);
	}
}
