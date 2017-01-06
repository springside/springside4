package org.springside.modules.utils.time;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;

/**
 * Date的parse()与format(), 采用Apache Common Lang中线程安全, 性能更佳的FastDateFormat
 * 
 * 1. 常用格式的FastDateFormat定义
 * 
 * 2. 日期格式不固定时的String<->Date 转换函数.
 * 
 * @see FastDateFormat#parse(String)
 * @see FastDateFormat#format(java.util.Date)
 * @see FastDateFormat#format(long)
 */
public class DateFormatUtil {

	// 以T分隔日期和时间，并带时区信息，符合ISO8601规范
	public static final String PATTERN_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";
	public static final String PATTERN_ISO_ON_SECOND = "yyyy-MM-dd'T'HH:mm:ssZZ";

	// 以空格分隔日期和时间，不带时区信息
	public static final String PATTERN_SIMPLE = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String PATTERN_SIMPLE_ON_SECOND = "yyyy-MM-dd HH:mm:ss";

	// 单独的日期
	public static final String PATTERN_DATE = "yyyy-MM-dd";

	// 使用工厂方法FastDateFormat.getInstance(), 从缓存中获取实例
	public static final FastDateFormat ISO_DATEFORMAT = FastDateFormat.getInstance(PATTERN_ISO);
	public static final FastDateFormat ISO_DATEFORMAT_ON_SECOND = FastDateFormat.getInstance(PATTERN_ISO_ON_SECOND);

	public static final FastDateFormat SIMPLE_DATEFORMAT = FastDateFormat.getInstance(PATTERN_SIMPLE);
	public static final FastDateFormat SIMPLE_DATEFORMAT_ON_SECOND = FastDateFormat
			.getInstance(PATTERN_SIMPLE_ON_SECOND);

	public static final FastDateFormat DATE_DATEFORMAT = FastDateFormat.getInstance(PATTERN_DATE);

	/**
	 * 分析日期字符串, 仅用于不固定pattern不固定的情况.
	 * 
	 * 否则直接使用DateFormats中封装好的fastDateFormat.
	 * 
	 * FastDateFormat.getInstance()已经做了缓存，不会每次创建对象，但直接使用对象仍然能减少在缓存中的查找.
	 */
	public static Date pareDate(String dateString, String pattern) throws ParseException {
		return FastDateFormat.getInstance(pattern).parse(dateString);
	}

	/**
	 * 格式化日期, 仅用于不固定pattern的情况.
	 * 
	 * 否则直接使用本类中封装好的FastDateFormat.
	 * 
	 * FastDateFormat.getInstance()已经做了缓存，不会每次创建对象，但直接使用对象仍然能减少在缓存中的查找.
	 */
	public static String formatDate(Date date, String pattern) throws ParseException {
		return FastDateFormat.getInstance(pattern).format(date);
	}

	/**
	 * 格式化日期, 仅用于不固定pattern的情况.
	 * 
	 * 否否则直接使用本类中封装好的FastDateFormat.
	 * 
	 * FastDateFormat.getInstance()已经做了缓存，不会每次创建对象，但直接使用对象仍然能减少在缓存中的查找.
	 */
	public static String formatDate(long date, String pattern) throws ParseException {
		return FastDateFormat.getInstance(pattern).format(date);
	}
}
