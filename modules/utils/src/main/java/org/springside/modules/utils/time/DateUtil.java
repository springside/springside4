package org.springside.modules.utils.time;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import com.google.common.annotations.Beta;

/**
 * 日期工具类.
 * 
 * 在不方便使用joda-time时，使用本类降低Date处理的复杂度与性能消耗.
 * 
 * 1. 日期格式不固定时的String<->Date 转换函数.
 * 
 * 2. 封装Common Lang一些最常用日期方法
 * 
 * @author calvin
 */
@Beta
public class DateUtil {

	public static final long MILLIS_PER_SECOND = 1000; // Number of milliseconds in a standard second.

	public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND; // Number of milliseconds in a standard minute.

	public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE; // Number of milliseconds in a standard hour.

	public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR; // Number of milliseconds in a standard day.

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
	 * 否则直接使用DateFormats中封装好的fastDateFormat.
	 * 
	 * FastDateFormat.getInstance()已经做了缓存，不会每次创建对象，但直接使用对象仍然能减少在缓存中的查找.
	 */
	public static String formatDate(Date date, String pattern) throws ParseException {
		return FastDateFormat.getInstance(pattern).format(date);
	}

	/**
	 * 格式化日期, 仅用于不固定pattern的情况.
	 * 
	 * 否则直接使用DateFormats中封装好的fastDateFormat.
	 * 
	 * FastDateFormat.getInstance()已经做了缓存，不会每次创建对象，但直接使用对象仍然能减少在缓存中的查找.
	 */
	public static String formatDate(long date, String pattern) throws ParseException {
		return FastDateFormat.getInstance(pattern).format(date);
	}

	/**
	 * 是否同一天.
	 */
	public static boolean isSameDay(final Date date1, final Date date2) {
		return DateUtils.isSameDay(date1, date2);
	}

	/**
	 * 日期往下取整.
	 * 
	 * 如 2016-12-10 07:33:23, 如果filed为Calendar.HOUR，则返回2016-12-10 07:00:00
	 * 
	 * 如果filed为Calendar.MONTH，则返回2016-12-01 00:00:00
	 * 
	 * @param field Calendar.HOUR,Calendar.Date etc...
	 */
	public static Date truncate(final Date date, final int field) {
		return DateUtils.truncate(date, field);
	}

	/**
	 * 日期往上取整.
	 * 
	 * 如 2016-12-10 07:33:23, 如果filed为Calendar.HOUR，则返回2016-12-10 08:00:00
	 * 
	 * 如果filed为 Calendar.MONTH，则返回2017-01-01 00:00:00
	 * 
	 * @param field Calendar.HOUR,Calendar.Date etc...
	 */
	public static Date ceiling(final Date date, final int field) {
		return DateUtils.ceiling(date, field);
	}

	/**
	 * 续一月
	 */
	public static Date addMonths(final Date date, final int amount) {
		return DateUtils.addMonths(date, amount);
	}

	/**
	 * 续一周
	 */
	public static Date addWeeks(final Date date, final int amount) {
		return DateUtils.addWeeks(date, amount);
	}

	/**
	 * 续一天
	 */
	public static Date addDays(final Date date, final int amount) {
		return DateUtils.addDays(date, amount);
	}

	/**
	 * 续一个小时
	 */
	public static Date addHours(final Date date, final int amount) {
		return DateUtils.addHours(date, amount);
	}

	/**
	 * 续一分钟
	 */
	public static Date addMinutes(final Date date, final int amount) {
		return DateUtils.addMinutes(date, amount);
	}

	/**
	 * 终于到了，续一秒.
	 */
	public static Date addSeconds(final Date date, final int amount) {
		return DateUtils.addSeconds(date, amount);
	}
}
