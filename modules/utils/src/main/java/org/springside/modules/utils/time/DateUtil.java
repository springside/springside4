package org.springside.modules.utils.time;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.springside.modules.utils.base.annotation.NotNull;

import com.google.common.annotations.Beta;

/**
 * 日期工具类.
 * 
 * 在不方便使用joda-time时，使用本类降低Date处理的复杂度与性能消耗, 封装Common Lang一些最常用日期方法
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
	 * 是否同一天.
	 * 
	 * @see DateUtils#isSameDay(Date, Date)
	 */
	public static boolean isSameDay(@NotNull final Date date1, @NotNull final Date date2) {
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
	public static Date truncate(@NotNull final Date date, final int field) {
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
	public static Date ceiling(@NotNull final Date date, final int field) {
		return DateUtils.ceiling(date, field);
	}

	/**
	 * 续一月
	 */
	public static Date addMonths(@NotNull final Date date, final int amount) {
		return DateUtils.addMonths(date, amount);
	}

	/**
	 * 减一月
	 */
	public static Date subMonths(@NotNull final Date date, final int amount) {
		return DateUtils.addMonths(date, -amount);
	}

	/**
	 * 续一周
	 */
	public static Date addWeeks(@NotNull final Date date, final int amount) {
		return DateUtils.addWeeks(date, amount);
	}

	/**
	 * 减一周
	 */
	public static Date subWeeks(@NotNull final Date date, final int amount) {
		return DateUtils.addWeeks(date, -amount);
	}

	/**
	 * 续一天
	 */
	public static Date addDays(@NotNull final Date date, final int amount) {
		return DateUtils.addDays(date, amount);
	}

	/**
	 * 减一天
	 */
	public static Date subDays(@NotNull final Date date, final int amount) {
		return DateUtils.addDays(date, -amount);
	}

	/**
	 * 续一个小时
	 */
	public static Date addHours(@NotNull final Date date, final int amount) {
		return DateUtils.addHours(date, amount);
	}

	/**
	 * 减一个小时
	 */
	public static Date subHours(@NotNull final Date date, final int amount) {
		return DateUtils.addHours(date, -amount);
	}

	/**
	 * 续一分钟
	 */
	public static Date addMinutes(@NotNull final Date date, final int amount) {
		return DateUtils.addMinutes(date, amount);
	}

	/**
	 * 减一分钟
	 */
	public static Date subMinutes(@NotNull final Date date, final int amount) {
		return DateUtils.addMinutes(date, -amount);
	}

	/**
	 * 终于到了，续一秒.
	 */
	public static Date addSeconds(@NotNull final Date date, final int amount) {
		return DateUtils.addSeconds(date, amount);
	}

	/**
	 * 减一秒.
	 */
	public static Date subSeconds(@NotNull final Date date, final int amount) {
		return DateUtils.addSeconds(date, -amount);
	}

	/**
	 * 设置年份.
	 */
	public static Date setYears(@NotNull final Date date, final int amount) {
		return DateUtils.setYears(date, amount);
	}

	/**
	 * 设置月份.
	 */
	public static Date setMonths(@NotNull final Date date, final int amount) {
		return DateUtils.setMonths(date, amount);
	}

	/**
	 * 设置日期.
	 */
	public static Date setDays(@NotNull final Date date, final int amount) {
		return DateUtils.setDays(date, amount);
	}

	/**
	 * 设置小时.
	 */
	public static Date setHours(@NotNull final Date date, final int amount) {
		return DateUtils.setHours(date, amount);
	}

	/**
	 * 设置分钟.
	 */
	public static Date setMinutes(@NotNull final Date date, final int amount) {
		return DateUtils.setMinutes(date, amount);
	}

	/**
	 * 设置秒.
	 */
	public static Date setSeconds(@NotNull final Date date, final int amount) {
		return DateUtils.setSeconds(date, amount);
	}

	/**
	 * 设置毫秒.
	 */
	public static Date setMilliseconds(@NotNull final Date date, final int amount) {
		return DateUtils.setMilliseconds(date, amount);
	}
}
