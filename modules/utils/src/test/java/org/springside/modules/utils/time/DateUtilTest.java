package org.springside.modules.utils.time;

import static org.assertj.core.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class DateUtilTest {

	@Test
	public void isSameDay() {
		Date date1 = new Date(106, 10, 1);
		Date date2 = new Date(106, 10, 1, 12, 23, 44);
		assertThat(DateUtil.isSameDay(date1, date2)).isTrue();

		Date date3 = new Date(106, 10, 1);
		assertThat(DateUtil.isSameTime(date1, date3)).isTrue();

		Date date5 = new Date(106, 10, 2);
		assertThat(DateUtil.isSameTime(date1, date5)).isFalse();

		Date date4 = new Date(106, 10, 1, 12, 23, 43);
		assertThat(DateUtil.isBetween(date3, date1, date2)).isTrue();
		assertThat(DateUtil.isBetween(date4, date1, date2)).isTrue();

		try {
			DateUtil.isBetween(null, date1, date2);
			fail("should fail before");
		} catch (Exception e) {

		}

		try {
			DateUtil.isBetween(date3, date2, date1);
			fail("should fail before");
		} catch (Exception e) {

		}

		assertThat(DateUtil.isBetween(date5, date1, date2)).isFalse();
	}

	@Test
	public void truncate() {
		Date date = new Date(106, 10, 1, 12, 12, 12);
		Date expectedDate = new Date(106, 10, 1);

		Date resultDate = DateUtil.truncate(date, Calendar.DATE);

		assertThat(resultDate.getTime()).isEqualTo(expectedDate.getTime());
	}

	@Test
	public void celling() {
		Date date = new Date(106, 10, 1, 12, 12, 12);
		Date expectedDate = new Date(106, 10, 2);

		Date resultDate = DateUtil.ceiling(date, Calendar.DATE);

		assertThat(resultDate.getTime()).isEqualTo(expectedDate.getTime());
	}

	@Test
	public void changeDay() {
		Date date = new Date(106, 10, 1, 12, 23, 44);
		Date expectDate1 = new Date(106, 10, 3);
		Date expectDate2 = new Date(106, 9, 31);
		Date expectDate3 = new Date(106, 11, 1);
		Date expectDate4 = new Date(106, 7, 1);
		Date expectDate5 = new Date(106, 10, 1, 13, 23, 44);
		Date expectDate6 = new Date(106, 10, 1, 10, 23, 44);
		Date expectDate7 = new Date(106, 10, 1, 12, 24, 44);
		Date expectDate8 = new Date(106, 10, 1, 12, 21, 44);

		Date expectDate9 = new Date(106, 10, 1, 12, 23, 45);
		Date expectDate10 = new Date(106, 10, 1, 12, 23, 42);

		Date expectDate11 = new Date(106, 10, 8);
		Date expectDate12 = new Date(106, 9, 25);

		assertThat(DateUtil.isSameDay(DateUtil.addDays(date, 2), expectDate1)).isTrue();
		assertThat(DateUtil.isSameDay(DateUtil.subDays(date, 1), expectDate2)).isTrue();

		assertThat(DateUtil.isSameDay(DateUtil.addWeeks(date, 1), expectDate11)).isTrue();
		assertThat(DateUtil.isSameDay(DateUtil.subWeeks(date, 1), expectDate12)).isTrue();

		assertThat(DateUtil.isSameDay(DateUtil.addMonths(date, 1), expectDate3)).isTrue();
		assertThat(DateUtil.isSameDay(DateUtil.subMonths(date, 3), expectDate4)).isTrue();

		assertThat(DateUtil.isSameTime(DateUtil.addHours(date, 1), expectDate5)).isTrue();
		assertThat(DateUtil.isSameTime(DateUtil.subHours(date, 2), expectDate6)).isTrue();

		assertThat(DateUtil.isSameTime(DateUtil.addMinutes(date, 1), expectDate7)).isTrue();
		assertThat(DateUtil.isSameTime(DateUtil.subMinutes(date, 2), expectDate8)).isTrue();

		assertThat(DateUtil.isSameTime(DateUtil.addSeconds(date, 1), expectDate9)).isTrue();
		assertThat(DateUtil.isSameTime(DateUtil.subSeconds(date, 2), expectDate10)).isTrue();

	}

	@Test
	public void setDay() {
		Date date = new Date(116, 10, 1, 10, 10, 1);
		Date expectedDate = new Date(116, 10, 3);
		Date expectedDate2 = new Date(116, 11, 1);
		Date expectedDate3 = new Date(117, 10, 1);
		Date expectedDate4 = new Date(116, 10, 1, 9, 10, 1);
		Date expectedDate5 = new Date(116, 10, 1, 10, 9, 1);
		Date expectedDate6 = new Date(116, 10, 1, 10, 10, 10);

		assertThat(DateUtil.isSameDay(DateUtil.setDays(date, 3), expectedDate)).isTrue();
		assertThat(DateUtil.isSameDay(DateUtil.setMonths(date, 11), expectedDate2)).isTrue();
		assertThat(DateUtil.isSameDay(DateUtil.setYears(date, 2017), expectedDate3)).isTrue();

		assertThat(DateUtil.isSameTime(DateUtil.setHours(date, 9), expectedDate4)).isTrue();
		assertThat(DateUtil.isSameTime(DateUtil.setMinutes(date, 9), expectedDate5)).isTrue();
		assertThat(DateUtil.isSameTime(DateUtil.setSeconds(date, 10), expectedDate6)).isTrue();

	}

	@Test
	public void getDayOfWeek() {
		// 2017-01-09
		Date date = new Date(117, 0, 9);
		int dayOfWeek = DateUtil.getDayOfWeek(date);
		assertThat(dayOfWeek).isEqualTo(2);
	}

	@Test
	public void isLeapYear() {
		// 2008-01-09,整除4年, true
		Date date = new Date(108, 0, 9);
		assertThat(DateUtil.isLeapYear(date)).isTrue();

		// 2000-01-09,整除400年，true
		date = new Date(100, 0, 9);
		assertThat(DateUtil.isLeapYear(date)).isTrue();

		// 1900-01-09，整除100年，false
		date = new Date(0, 0, 9);
		assertThat(DateUtil.isLeapYear(date)).isFalse();
	}

	@Test
	public void getXXofXX() {
		// 2008-02-09, 整除4年, 闰年
		Date date = new Date(108, 2, 9);
		assertThat(DateUtil.getMonthLength(date)).isEqualTo(29);

		// 2009-02-09, 整除4年, 非闰年
		Date date2 = new Date(109, 2, 9);
		assertThat(DateUtil.getMonthLength(date2)).isEqualTo(28);

		Date date3 = new Date(108, 8, 9);
		assertThat(DateUtil.getMonthLength(date3)).isEqualTo(31);

		Date date4 = new Date(109, 11, 30);
		assertThat(DateUtil.getDayOfYear(date4)).isEqualTo(364);

		Date date5 = new Date(117, 0, 12);
		assertThat(DateUtil.getWeekOfMonth(date5)).isEqualTo(2);
		assertThat(DateUtil.getWeekOfYear(date5)).isEqualTo(2);
	}
}
