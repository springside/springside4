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
}
