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
		Date date = new Date(106, 10, 1,12,12,12);
		Date expectDate = new Date(106, 10, 1);
		
		assertThat(DateUtil.truncate(date, Calendar.DATE).getTime()).isEqualTo(expectDate.getTime());
	}
	
	@Test
	public void celling() {
		Date date = new Date(106, 10, 1,12,12,12);
		Date expectDate = new Date(2016, 10, 2);
		
		assertThat(DateUtil.ceiling(date, Calendar.DATE).getTime()).isEqualTo(expectDate.getTime());
	}
	
	@Test
	public void addDay() {
		Date date = new Date(106, 10, 1);
		Date expectDate = new Date(106, 10, 3);
		
		assertThat(DateUtil.isSameDay(DateUtil.addDays(date, 2),expectDate)).isTrue();
	}
	
	@Test
	public void setDay() {
		Date date = new Date(106, 10, 1);
		Date expectDate = new Date(106, 10, 3);
		
		assertThat(DateUtil.isSameDay(DateUtil.setDays(date, 3),expectDate)).isTrue();
	}

}
