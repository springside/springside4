package org.springside.modules.utils.number;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

/**
 * 数字的工具类.
 * 
 * 1.原始类型数字与byte[]的双向转换(via Guava)
 * 
 * 2.判断字符串是否数字, 是否16进制字符串(via Common Lang)
 * 
 * 3.10机制/16进制字符串 与 原始类型数字/数字对象 的双向转换(参考Common Lang自写)
 */
public class NumberUtil {

	///////////// bytes[] 与原始类型数字转换 ///////

	public static byte[] toBytes(int value) {
		return Ints.toByteArray(value);
	}

	public static byte[] toBytes(long value) {
		return Longs.toByteArray(value);
	}

	/**
	 * from ElasticSearch Numbers
	 */
	public static byte[] toBytes(double val) {
		return toBytes(Double.doubleToRawLongBits(val));
	}

	public static int toInt(byte[] bytes) {
		return Ints.fromByteArray(bytes);
	}

	public static long toLong(byte[] bytes) {
		return Longs.fromByteArray(bytes);
	}

	/**
	 * from ElasticSearch Numbers
	 */
	public static double toDouble(byte[] bytes) {
		return Double.longBitsToDouble(toLong(bytes));
	}

	/////// 判断字符串类型//////////
	/**
	 * 判断字符串是否合法数字
	 */
	public static boolean isNumber(String str) {
		return NumberUtils.isNumber(str);
	}

	/**
	 * 判断字符串是否16进制
	 */
	public static boolean isHexNumber(String value) {
		int index = value.startsWith("-") ? 1 : 0;
		return value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index);
	}

	/////////// 将字符串安全的转化为原始类型数字/////////

	/**
	 * 将10进制的String安全的转化为int，当str为空或非数字字符串时，返回0
	 */
	public static int toInt(String str) {
		return NumberUtils.toInt(str, 0);
	}

	/**
	 * 将10进制的String安全的转化为int，当str为空或非数字字符串时，返回default值
	 */
	public static int toInt(String str, int defaultValue) {
		return NumberUtils.toInt(str, defaultValue);
	}

	/**
	 * 将10进制的String安全的转化为long，当str为空或非数字字符串时，返回0
	 */
	public static long toLong(String str) {
		return NumberUtils.toLong(str, 0L);
	}

	/**
	 * 将10进制的String安全的转化为long，当str为空或非数字字符串时，返回default值
	 */
	public static long toLong(String str, long defaultValue) {
		return NumberUtils.toLong(str, defaultValue);
	}

	/**
	 * 将10进制的String安全的转化为double，当str为空或非数字字符串时，返回0
	 */
	public static double toDouble(String str) {
		return NumberUtils.toDouble(str, 0L);
	}

	/**
	 * 将10进制的String安全的转化为double，当str为空或非数字字符串时，返回default值
	 */
	public static double toDouble(String str, double defaultValue) {
		return NumberUtils.toDouble(str, defaultValue);
	}

	////////////// 10进制字符串 转换对象类型数字/////////////
	/**
	 * 将10进制的String安全的转化为Integer，当str为空或非数字字符串时，返回null
	 */
	public static Integer toIntObject(String str) {
		return toIntObject(str, null);
	}

	/**
	 * 将10进制的String安全的转化为Integer，当str为空或非数字字符串时，返回default值
	 */
	public static Integer toIntObject(String str, Integer defaultValue) {
		if (StringUtils.isEmpty(str)) {
			return defaultValue;
		}
		try {
			return Integer.valueOf(str);
		} catch (final NumberFormatException nfe) {
			return defaultValue;
		}
	}

	/**
	 * 将10进制的String安全的转化为Long，当str为空或非数字字符串时，返回null
	 */
	public static Long toLongObject(String str) {
		return toLongObject(str, null);
	}

	/**
	 * 将10进制的String安全的转化为Long，当str为空或非数字字符串时，返回default值
	 */
	public static Long toLongObject(String str, Long defaultValue) {
		if (StringUtils.isEmpty(str)) {
			return defaultValue;
		}
		try {
			return Long.valueOf(str);
		} catch (final NumberFormatException nfe) {
			return defaultValue;
		}
	}

	/**
	 * 将10进制的String安全的转化为Double，当str为空或非数字字符串时，返回null
	 */
	public static Double toDoubleObject(String str) {
		return toDoubleObject(str, null);
	}

	/**
	 * 将10进制的String安全的转化为Long，当str为空或非数字字符串时，返回default值
	 */
	public static Double toDoubleObject(String str, Double defaultValue) {
		if (StringUtils.isEmpty(str)) {
			return defaultValue;
		}
		try {
			return Double.valueOf(str);
		} catch (final NumberFormatException nfe) {
			return defaultValue;
		}
	}

	//////////// 16进制 字符串转换为数字对象//////////

	/**
	 * 将16进制的String转化为Integer，出错时返回null.
	 */
	public static Integer hexToIntObject(String str) {
		return hexToIntObject(str, null);
	}

	/**
	 * 将16进制的String转化为Integer，出错时返回默认值.
	 */
	public static Integer hexToIntObject(String str, Integer defaultValue) {
		if (StringUtils.isEmpty(str)) {
			return defaultValue;
		}
		try {
			return Integer.decode(str);
		} catch (final NumberFormatException nfe) {
			return defaultValue;
		}
	}

	/**
	 * 将16进制的String转化为Long，出错时返回null.
	 */
	public static Long hexToLongObject(String str) {
		return hexToLongObject(str, null);
	}

	/**
	 * 将16进制的String转化为Long，出错时返回默认值.
	 */
	public static Long hexToLongObject(String str, Long defaultValue) {
		if (StringUtils.isEmpty(str)) {
			return defaultValue;
		}
		try {
			return Long.decode(str);
		} catch (final NumberFormatException nfe) {
			return defaultValue;
		}
	}

	/////// toString (定义了原子类型与对象类型的参数，保证不会用错函数) ///////

	public static String toString(int i) {
		return Integer.toString(i);
	}

	public static String toString(Integer i) {
		return i.toString();
	}

	public static String toString(long l) {
		return Long.toString(l);
	}

	public static String toString(Long l) {
		return l.toString();
	}

	public static String toString(double d) {
		return Double.toString(d);
	}

	public static String toString(Double d) {
		return d.toString();
	}

	/**
	 * 输出格式化为小数后两位的double字符串
	 */
	public static String to2DigitString(double d) {
		return String.format(Locale.ROOT, "%.2f", d);
	}

	/////////// 杂项 ///////

	/**
	 * 安全的将小于Integer.MAX的long转为int，否则抛出IllegalArgumentException异常
	 */
	public static int toInt32(long x) {
		if ((int) x == x){
			return (int) x;
		}
		throw new IllegalArgumentException("Int " + x + " out of range");
	}
}
