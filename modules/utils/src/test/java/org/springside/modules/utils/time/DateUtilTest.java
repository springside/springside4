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
	public void addDay() {
		Date date = new Date(106, 10, 1);
		Date expectDate = new Date(106, 10, 3);

		assertThat(DateUtil.isSameDay(DateUtil.addDays(date, 2), expectDate)).isTrue();
	}

	@Test
	public void setDay() {
		Date date = new Date(116, 10, 1);
		Date expectedDate = new Date(116, 10, 3);

		Date resultDate = DateUtil.setDays(date, 3);

		assertThat(DateUtil.isSameDay(resultDate, expectedDate)).isTrue();
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
	public void getMonthLength() {
		// 2008-02-09, 整除4年, 闰年
		Date date = new Date(108, 2, 9);
		assertThat(DateUtil.getMonthLength(date)).isEqualTo(29);

		// 2009-02-09, 整除4年, 非闰年
		Date date2 = new Date(109, 2, 9);
		assertThat(DateUtil.getMonthLength(date2)).isEqualTo(28);
		
		Date date3 = new Date(108, 8, 9);
		assertThat(DateUtil.getMonthLength(date3)).isEqualTo(31);
	}
}
