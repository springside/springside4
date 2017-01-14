package org.springside.modules.utils.time;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;
import org.springside.modules.utils.base.annotation.NotNull;

/**
 * 日期工具类.
 * 
 * 在不方便使用joda-time时，使用本类降低Date处理的复杂度与性能消耗, 封装Common Lang及移植Jodd的最常用日期方法
 * 
 * @author calvin
 */
public abstract class DateUtil {

	public static final long MILLIS_PER_SECOND = 1000; // Number of milliseconds in a standard second.

	public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND; // Number of milliseconds in a standard minute.

	public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE; // Number of milliseconds in a standard hour.

	public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR; // Number of milliseconds in a standard day.

	private static final int[] MONTH_LENGTH = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

	//////// 日期比较 ///////////
	/**
	 * 是否同一天.
	 * 
	 * @see DateUtils#isSameDay(Date, Date)
	 */
	public static boolean isSameDay(@NotNull final Date date1, @NotNull final Date date2) {
		return DateUtils.isSameDay(date1, date2);
	}

	/**
	 * 是否同一时刻.
	 */
	public static boolean isSameTime(@NotNull final Date date1, @NotNull final Date date2) {
		// date.getMillisOf() 比date.getTime()快
		return date1.compareTo(date2) == 0;
	}

	/**
	 * 判断日期是否在范围内，包含相等的日期
	 */
	public static boolean isBetween(@NotNull final Date date, @NotNull final Date start, @NotNull final Date end) {
		if (date == null || start == null || end == null || start.after(end)) {
			throw new IllegalArgumentException("some date parameters is null or dateBein after dateEnd");
		}
		return !date.before(start) && !date.after(end);
	}
	
	/////////// 日期设置处理 /////////

	/**
	 * 日期往下取整.
	 * 
	 * 如 2016-12-10 07:33:23, 如果filed为Calendar.HOUR，则返回2016-12-10 07:00:00
	 * 
	 * 如果filed为Calendar.MONTH，则返回2016-12-01 00:00:00
	 * 
	 * @param field Calendar.HOUR,Calendar.Date etc...
	 */
	public static Date truncate(@NotNull final Date date, int field) {
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
	public static Date ceiling(@NotNull final Date date, int field) {
		return DateUtils.ceiling(date, field);
	}

	//////////// 往前往后滚动时间//////////////

	/**
	 * 续一月
	 */
	public static Date addMonths(@NotNull final Date date, int amount) {
		return DateUtils.addMonths(date, amount);
	}

	/**
	 * 减一月
	 */
	public static Date subMonths(@NotNull final Date date, int amount) {
		return DateUtils.addMonths(date, -amount);
	}

	/**
	 * 续一周
	 */
	public static Date addWeeks(@NotNull final Date date, int amount) {
		return DateUtils.addWeeks(date, amount);
	}

	/**
	 * 减一周
	 */
	public static Date subWeeks(@NotNull final Date date, int amount) {
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
	public static Date subDays(@NotNull final Date date, int amount) {
		return DateUtils.addDays(date, -amount);
	}

	/**
	 * 续一个小时
	 */
	public static Date addHours(@NotNull final Date date, int amount) {
		return DateUtils.addHours(date, amount);
	}

	/**
	 * 减一个小时
	 */
	public static Date subHours(@NotNull final Date date, int amount) {
		return DateUtils.addHours(date, -amount);
	}

	/**
	 * 续一分钟
	 */
	public static Date addMinutes(@NotNull final Date date, int amount) {
		return DateUtils.addMinutes(date, amount);
	}

	/**
	 * 减一分钟
	 */
	public static Date subMinutes(@NotNull final Date date, int amount) {
		return DateUtils.addMinutes(date, -amount);
	}

	/**
	 * 终于到了，续一秒.
	 */
	public static Date addSeconds(@NotNull final Date date, int amount) {
		return DateUtils.addSeconds(date, amount);
	}

	/**
	 * 减一秒.
	 */
	public static Date subSeconds(@NotNull final Date date, int amount) {
		return DateUtils.addSeconds(date, -amount);
	}

	//////////// 直接设置时间//////////////

	/**
	 * 设置年份, 公元纪年.
	 */
	public static Date setYears(@NotNull final Date date, int amount) {
		return DateUtils.setYears(date, amount);
	}

	/**
	 * 设置月份, 0-11.
	 */
	public static Date setMonths(@NotNull final Date date, int amount) {
		return DateUtils.setMonths(date, amount);
	}

	/**
	 * 设置日期, 1-31.
	 */
	public static Date setDays(@NotNull final Date date, int amount) {
		return DateUtils.setDays(date, amount);
	}

	/**
	 * 设置小时, 0-23.
	 */
	public static Date setHours(@NotNull final Date date, int amount) {
		return DateUtils.setHours(date, amount);
	}

	/**
	 * 设置分钟, 0-59.
	 */
	public static Date setMinutes(@NotNull final Date date, int amount) {
		return DateUtils.setMinutes(date, amount);
	}

	/**
	 * 设置秒.
	 */
	public static Date setSeconds(@NotNull final Date date, int amount) {
		return DateUtils.setSeconds(date, amount);
	}

	/**
	 * 设置毫秒.
	 */
	public static Date setMilliseconds(@NotNull final Date date, int amount) {
		return DateUtils.setMilliseconds(date, amount);
	}

	///// 获取日期的//////
	/**
	 * 获得日期是一周的第几天, 返回值为1是Sunday , 2是Monday....
	 * 
	 * 可通过Canendar的setFirstDayOfWeek()来改变Monday开始为1
	 */
	public static int getDayOfWeek(@NotNull final Date date) {
		return get(date, Calendar.DAY_OF_WEEK);
	}

	/**
	 * 获得日期是一年的第几天，返回值从1开始
	 */
	public static int getDayOfYear(@NotNull final Date date) {
		return get(date, Calendar.DAY_OF_YEAR);
	}

	/**
	 * 获得日期是一年的第几天，返回值从1开始.
	 * 
	 * 开始的一周，只要有一天在那个月里都算.
	 */
	public static int getWeekOfMonth(@NotNull final Date date) {
		return get(date, Calendar.WEEK_OF_MONTH);
	}

	/**
	 * 获得日期是一年的第几周，返回值从1开始.
	 * 
	 * 开始的一周，只要有一天在那一年里都算.
	 */
	public static int getWeekOfYear(@NotNull final Date date) {
		return get(date, Calendar.WEEK_OF_YEAR);
	}

	private static int get(final Date date, int field) {
		Validate.notNull(date, "The date must not be null");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(field);
	}

	////// 闰年及每月天数///////
	/**
	 * 是否闰年.
	 */
	public static boolean isLeapYear(@NotNull final Date date) {
		return isLeapYear(get(date, Calendar.YEAR));
	}

	/**
	 * 是否闰年，移植Jodd Core的TimeUtil
	 * 
	 * 参数是公元计数, 如2016
	 */
	public static boolean isLeapYear(int y) {
		boolean result = false;

		if (((y % 4) == 0) && // must be divisible by 4...
				((y < 1582) || // and either before reform year...
						((y % 100) != 0) || // or not a century...
						((y % 400) == 0))) { // or a multiple of 400...
			result = true; // for leap year.
		}
		return result;
	}

	/**
	 * 获取某个月有多少天, 考虑闰年等因数, 移植Jodd Core的TimeUtil
	 */
	public static int getMonthLength(@NotNull final Date date) {
		int year = get(date, Calendar.YEAR);
		int month = get(date, Calendar.MONTH);
		return getMonthLength(year, month);
	}

	/**
	 * 获取某个月有多少天, 考虑闰年等因数, 移植Jodd Core的TimeUtil
	 */
	public static int getMonthLength(int year, int month) {

		if ((month < 1) || (month > 12)) {
			throw new IllegalArgumentException("Invalid month: " + month);
		}
		if (month == 2) {
			return isLeapYear(year) ? 29 : 28;
		}

		return MONTH_LENGTH[month];
	}
}
