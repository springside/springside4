package org.springside.modules.utils.time;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springside.modules.utils.base.annotation.NotNull;

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
	public static final String PATTERN_ISO_WITH_SECOND = "yyyy-MM-dd'T'HH:mm:ssZZ";
	public static final String PATTERN_ISO_WITH_DATE = "yyyy-MM-dd";

	// 以空格分隔日期和时间，不带时区信息
	public static final String PATTERN_SIMPLE = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String PATTERN_SIMPLE_WITH_SECOND = "yyyy-MM-dd HH:mm:ss";

	// 使用工厂方法FastDateFormat.getInstance(), 从缓存中获取实例

	// 以T分隔日期和时间，并带时区信息，符合ISO8601规范
	public static final FastDateFormat ISO_FORMAT = FastDateFormat.getInstance(PATTERN_ISO);
	public static final FastDateFormat ISO_WITH_SECOND_FORMAT = FastDateFormat.getInstance(PATTERN_ISO_WITH_SECOND);
	public static final FastDateFormat ISO_WITH_DATE_FORMAT = FastDateFormat.getInstance(PATTERN_ISO_WITH_DATE);

	// 以空格分隔日期和时间，不带时区信息
	public static final FastDateFormat SIMPLE_FORMAT = FastDateFormat.getInstance(PATTERN_SIMPLE);
	public static final FastDateFormat SIMPLE_WITH_SECOND_FORMAT = FastDateFormat
			.getInstance(PATTERN_SIMPLE_WITH_SECOND);

	/**
	 * 分析日期字符串, 仅用于pattern不固定的情况.
	 * 
	 * 否则直接使用DateFormats中封装好的FastDateFormat.
	 * 
	 * FastDateFormat.getInstance()已经做了缓存，不会每次创建对象，但直接使用对象仍然能减少在缓存中的查找.
	 */
	public static Date pareDate(@NotNull String pattern, @NotNull String dateString) throws ParseException {
		return FastDateFormat.getInstance(pattern).parse(dateString);
	}

	/**
	 * 格式化日期, 仅用于pattern不固定的情况.
	 * 
	 * 否则直接使用本类中封装好的FastDateFormat.
	 * 
	 * FastDateFormat.getInstance()已经做了缓存，不会每次创建对象，但直接使用对象仍然能减少在缓存中的查找.
	 */
	public static String formatDate(@NotNull String pattern, @NotNull Date date) {
		return FastDateFormat.getInstance(pattern).format(date);
	}

	/**
	 * 格式化日期, 仅用于不固定pattern不固定的情况.
	 * 
	 * 否否则直接使用本类中封装好的FastDateFormat.
	 * 
	 * FastDateFormat.getInstance()已经做了缓存，不会每次创建对象，但直接使用对象仍然能减少在缓存中的查找.
	 */
	public static String formatDate(@NotNull String pattern, long date) {
		return FastDateFormat.getInstance(pattern).format(date);
	}
	
	/**
	 * 按HH:mm:ss.SSS格式，格式化时间间隔
	 */
	public static String formatDuration(long durationMillis){
		return DurationFormatUtils.formatDurationHMS(durationMillis);
	}
	
	/**
	 * 按HH:mm:ss格式，格式化时间间隔
	 */
	public static String formatDurationOnSecond(long durationMillis){
		return DurationFormatUtils.formatDuration(durationMillis,"HH:mm:ss");
	}
}
