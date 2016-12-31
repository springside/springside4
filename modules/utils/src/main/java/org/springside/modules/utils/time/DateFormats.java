package org.springside.modules.utils.time;

import org.apache.commons.lang3.time.FastDateFormat;

/**
 * Date的parse()与format(), 采用Apache Common Lang中线程安全,性能更佳的FastDateFormat
 * 
 * @see FastDateFormat#parse(String)
 * @see FastDateFormat#format(java.util.Date)
 * @see FastDateFormat#format(long)
 */
public class DateFormats {

	// 以T分隔日期和时间，并带时区信息，符合ISO8601规范
	public static String ISO_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";
	public static String ISO_SECOND_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZZ";

	// 以空格分隔日期和时间，不带时区信息
	public static String SIMPLE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
	public static String SIMPLE_SECOND_PATTERN = "yyyy-MM-dd HH:mm:ss";

	// 单独的日期或时间
	public static String DATE_PATTERN = "yyyy-MM-dd";
	public static String TIME_PATTERN = "HH:mm:ss.SSS";
	public static String TIME_SECOND_PATTERN = "HH:mm:ss";

	// 使用工厂方法FastDateFormat.getInstance(), 从缓存中获取实例
	public static FastDateFormat ISO_DATEFORMAT = FastDateFormat.getInstance(ISO_PATTERN);
	public static FastDateFormat ISO_SECOND_DATEFORMAT = FastDateFormat.getInstance(ISO_SECOND_PATTERN);

	public static FastDateFormat SIMPLE_DATEFORMAT = FastDateFormat.getInstance(SIMPLE_PATTERN);
	public static FastDateFormat SIMPLE_SECOND_DATEFORMAT = FastDateFormat.getInstance(SIMPLE_SECOND_PATTERN);

	public static FastDateFormat DATE_DATEFORMAT = FastDateFormat.getInstance(DATE_PATTERN);
	public static FastDateFormat TIME_DATEFORMAT = FastDateFormat.getInstance(TIME_PATTERN);
	public static FastDateFormat TIME_SECOND_DATEFORMAT = FastDateFormat.getInstance(TIME_SECOND_PATTERN);
}
