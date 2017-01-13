package org.springside.modules.utils.number;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Validate;
import org.springside.modules.utils.base.Platforms;

/**
 * 随机数工具集.
 * 
 * 1. 获取无锁的ThreadLocalRandom
 * 
 * 2. 获取性能较佳的SecureRandom
 * 
 * 3. 保证没有负数陷阱，也能更精确she定范围的nextInt 与 nextLong(copy from Common Lang RandomUtils)
 * 
 * @author calvin
 */
public abstract class RandomUtil {

	private static final Random RANDOM = new Random();

	/////////////////// 获取Random实例//////////////
	/**
	 * 返回无锁的ThreadLocalRandom
	 * 
	 * 如果JDK6，使用移植于JDK7的版本，否则使用JDK自带
	 */
	public static Random threadLocalRandom() {
		if (Platforms.IS_ATLEASET_JAVA7) {
			return java.util.concurrent.ThreadLocalRandom.current();
		} else {
			return org.springside.modules.utils.concurrent.jsr166e.ThreadLocalRandom.current();
		}
	}

	/**
	 * 使用性能更好的SHA1PRNG, Tomcat的sessionId生成也用此算法.
	 * 
	 * 但JDK7中，需要在启动参数加入 -Djava.security=file:/dev/./urandom （中间那个点很重要）
	 * 
	 * 详见：《SecureRandom的江湖偏方与真实效果》http://calvin1978.blogcn.com/articles/securerandom.html
	 */
	public static Random secureRandom() {
		try {
			return SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			return new SecureRandom();
		}
	}

	////////////////// nextInt 相关/////////
	/**
	 * 返回0到Intger.MAX_VALUE的随机Int, 使用内置全局普通Random.
	 */
	public static int nextInt() {
		return nextInt(RANDOM);
	}

	/**
	 * 返回0到Intger.MAX_VALUE的随机Int, 可传入SecureRandom或ThreadLocalRandom
	 */
	public static int nextInt(Random random) {
		int n = random.nextInt();
		if (n == Integer.MIN_VALUE) {
			n = 0; // corner case
		} else {
			n = Math.abs(n);
		}

		return n;
	}

	/**
	 * 返回0到max的随机Int, 使用内置全局普通Random.
	 */
	public static int nextInt(int max) {
		return nextInt(RANDOM, max);
	}

	/**
	 * 返回0到max的随机Int, 可传入SecureRandom或ThreadLocalRandom
	 */
	public static int nextInt(Random random, int max) {
		return random.nextInt(max);
	}

	/**
	 * 返回min到max的随机Int, 使用内置全局普通Random.
	 * 
	 * min必须大于0.
	 */
	public static int nextInt(int min, int max) {
		return nextInt(RANDOM, min, max);
	}

	/**
	 * 返回min到max的随机Int,可传入SecureRandom或ThreadLocalRandom.
	 * 
	 * min必须大于0.
	 * 
	 * JDK本身不具有控制两端范围的nextInt，因此参考Commons Lang RandomUtils的实现, 不直接复用是因为要传入Random实例
	 * 
	 * @see org.apache.commons.lang3.RandomUtils#nextInt(long, long)
	 */
	public static int nextInt(Random random, int min, int max) {
		Validate.isTrue(max >= min, "Start value must be smaller or equal to end value.");
		Validate.isTrue(min >= 0, "Both range values must be non-negative.");

		if (min == max) {
			return min;
		}

		return min + random.nextInt(max - min);
	}

	////////////////// long 相关/////////
	/**
	 * 返回0－Long.MAX_VALUE间的随机Long, 使用内置全局普通Random.
	 */
	public static long nextLong() {
		return nextLong(RANDOM);
	}

	/**
	 * 返回0－Long.MAX_VALUE间的随机Long, 可传入SecureRandom或ThreadLocalRandom
	 */
	public static long nextLong(Random random) {
		long n = random.nextLong();
		if (n == Long.MIN_VALUE) {
			n = 0; // corner case
		} else {
			n = Math.abs(n);
		}
		return n;
	}

	/**
	 * 返回0－max间的随机Long, 使用内置全局普通Random.
	 */
	public static long nextLong(long max) {
		return nextLong(RANDOM, 0, max);
	}

	/**
	 * 返回0-max间的随机Long, 可传入SecureRandom或ThreadLocalRandom
	 */
	public static long nextLong(Random random, long max) {
		return nextLong(random, 0, max);
	}

	/**
	 * 返回min－max间的随机Long, 使用内置全局普通Random.
	 * 
	 * min必须大于0.
	 */
	public static long nextLong(long min, long max) {
		return nextLong(RANDOM, min, max);
	}

	/**
	 * 返回min-max间的随机Long,可传入SecureRandom或ThreadLocalRandom.
	 * 
	 * min必须大于0.
	 * 
	 * JDK本身不具有控制两端范围的nextLong，因此参考Commons Lang RandomUtils的实现, 不直接复用是因为要传入Random实例
	 *
	 * @see org.apache.commons.lang3.RandomUtils#nextLong(long, long)
	 */
	public static long nextLong(Random random, long min, long max) {
		Validate.isTrue(max >= min, "Start value must be smaller or equal to end value.");
		Validate.isTrue(min >= 0, "Both range values must be non-negative.");

		if (min == max) {
			return min;
		}

		return (long) (min + ((max - min) * random.nextDouble()));
	}

	///////// Double //////
	/**
	 * 返回0-之间的double
	 */
	public static double nextDouble() {
		return nextDouble(RANDOM, 0, Double.MAX_VALUE);
	}

	/**
	 * 返回0-Double.MAX之间的double
	 */
	public static double nextDouble(Random random) {
		return nextDouble(random, 0, Double.MAX_VALUE);
	}

	/**
	 * 返回0-max之间的double
	 */
	public static double nextDouble(double max) {
		return nextDouble(RANDOM, 0, max);
	}

	/**
	 * 返回0-max之间的double
	 */
	public static double nextDouble(Random random, double max) {
		return nextDouble(random, 0, max);
	}

	/**
	 * 返回min-max之间的double
	 */
	public static double nextDouble(final double min, final double max) {
		return nextDouble(RANDOM, min, max);
	}

	/**
	 * 返回min-max之间的double
	 */
	public static double nextDouble(Random random, final double min, final double max) {
		Validate.isTrue(max >= min, "Start value must be smaller or equal to end value.");
		Validate.isTrue(min >= 0, "Both range values must be non-negative.");

		if (min == max) {
			return min;
		}

		return min + ((max - min) * random.nextDouble());
	}
	//////////////////// String/////////

	/**
	 * 随机字母或数字，固定长度
	 */
	public static String randomStringFixLength(int length) {
		return RandomStringUtils.random(length, 0, 0, true, true, null, RANDOM);
	}

	/**
	 * 随机字母或数字，固定长度
	 */
	public static String randomStringFixLength(Random random, int length) {
		return RandomStringUtils.random(length, 0, 0, true, true, null, random);
	}

	/**
	 * 随机字母或数字，随机长度
	 */
	public static String randomStringRandomLength(int minLength, int maxLength) {
		return RandomStringUtils.random(nextInt(minLength, maxLength), 0, 0, true, true, null, RANDOM);
	}

	/**
	 * 随机字母或数字，随机长度
	 */
	public static String randomStringRandomLength(Random random, int minLength, int maxLength) {
		return RandomStringUtils.random(nextInt(random, minLength, maxLength), 0, 0, true, true, null, random);
	}

	/**
	 * 随机字母，固定长度
	 */
	public static String randomLetterFixLength(int length) {
		return RandomStringUtils.random(length, 0, 0, true, false, null, RANDOM);
	}

	/**
	 * 随机字母，固定长度
	 */
	public static String randomLetterFixLength(Random random, int length) {
		return RandomStringUtils.random(length, 0, 0, true, false, null, random);
	}

	/**
	 * 随机字母，随机长度
	 */
	public static String randomLetterRandomLength(int minLength, int maxLength) {
		return RandomStringUtils.random(nextInt(minLength, maxLength), 0, 0, true, false, null, RANDOM);
	}

	/**
	 * 随机字母，随机长度
	 */
	public static String randomLetterRandomLength(Random random, int minLength, int maxLength) {
		return RandomStringUtils.random(nextInt(random, minLength, maxLength), 0, 0, true, false, null, random);
	}

	/**
	 * 随机ASCII字符(含字母，数字及其他符号)，固定长度
	 */
	public static String randomAsciiFixLength(int length) {
		return RandomStringUtils.random(length, 32, 127, false, false, null, RANDOM);
	}

	/**
	 * 随机ASCII字符(含字母，数字及其他符号)，固定长度
	 */
	public static String randomAsciiFixLength(Random random, int length) {
		return RandomStringUtils.random(length, 32, 127, false, false, null, random);
	}

	/**
	 * 随机ASCII字符(含字母，数字及其他符号)，随机长度
	 */
	public static String randomAsciiRandomLength(int minLength, int maxLength) {
		return RandomStringUtils.random(nextInt(minLength, maxLength), 32, 127, false, false, null, RANDOM);
	}

	/**
	 * 随机ASCII字符(含字母，数字及其他符号)，随机长度
	 */
	public static String randomAsciiRandomLength(Random random, int minLength, int maxLength) {
		return RandomStringUtils.random(nextInt(random, minLength, maxLength),32, 127, false, false, null, random);
	}
}
