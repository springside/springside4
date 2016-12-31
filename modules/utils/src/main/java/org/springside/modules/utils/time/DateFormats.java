package org.springside.modules.utils.time;

import org.apache.commons.lang3.time.FastDateFormat;

/**
 * Date的parse()与format(), 采用Apache Common Lang中线程安全, 性能更佳的FastDateFormat
 * 
 * @see FastDateFormat#parse(String)
 * @see FastDateFormat#format(java.util.Date)
 * @see FastDateFormat#format(long)
 */
public class DateFormats {

	// 以T分隔日期和时间，并带时区信息，符合ISO8601规范
	public static final String ISO_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";
	public static final String ISO_PATTERN_ON_SECOND = "yyyy-MM-dd'T'HH:mm:ssZZ";

	// 以空格分隔日期和时间，不带时区信息
	public static final String SIMPLE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String SIMPLE_PATTERN_ON_SECOND = "yyyy-MM-dd HH:mm:ss";

	// 单独的日期或时间
	public static final String DATE_PATTERN = "yyyy-MM-dd";
	public static final String TIME_PATTERN = "HH:mm:ss.SSS";
	public static final String TIME_PATTERN_ON_SECOND = "HH:mm:ss";

	// 使用工厂方法FastDateFormat.getInstance(), 从缓存中获取实例
	public static final FastDateFormat ISO_DATEFORMAT = FastDateFormat.getInstance(ISO_PATTERN);
	public static final FastDateFormat ISO_DATEFORMAT_ON_SECOND = FastDateFormat.getInstance(ISO_PATTERN_ON_SECOND);

	public static final FastDateFormat SIMPLE_DATEFORMAT = FastDateFormat.getInstance(SIMPLE_PATTERN);
	public static final FastDateFormat SIMPLE_DATEFORMAT_ON_SECOND = FastDateFormat.getInstance(SIMPLE_PATTERN_ON_SECOND);

	public static final FastDateFormat DATE_DATEFORMAT = FastDateFormat.getInstance(DATE_PATTERN);
	public static final FastDateFormat TIME_DATEFORMAT = FastDateFormat.getInstance(TIME_PATTERN);
	public static final FastDateFormat TIME_DATEFORMAT_ON_SECOND = FastDateFormat.getInstance(TIME_PATTERN_ON_SECOND);
}
