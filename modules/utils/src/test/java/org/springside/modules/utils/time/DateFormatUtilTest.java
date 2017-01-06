package org.springside.modules.utils.time;

import static org.assertj.core.api.Assertions.*;

import java.text.ParseException;
import java.util.Date;

import org.junit.Test;

public class DateFormatUtilTest {

	@Test
	public void isoDateFormat() {
		Date date = new Date(116, 10, 1, 12, 23, 44);
		assertThat(DateFormatUtil.ISO_DATEFORMAT.format(date)).isEqualTo("2016-11-01T12:23:44.000+08:00");
		assertThat(DateFormatUtil.ISO_DATEFORMAT_ON_SECOND.format(date)).isEqualTo("2016-11-01T12:23:44+08:00");
		assertThat(DateFormatUtil.ISO_DATEFORMAT_ON_DATE.format(date)).isEqualTo("2016-11-01");
	}

	@Test
	public void simpleDateFormat() {
		Date date = new Date(116, 10, 1, 12, 23, 44);
		assertThat(DateFormatUtil.SIMPLE_DATEFORMAT.format(date)).isEqualTo("2016-11-01 12:23:44.000");
		assertThat(DateFormatUtil.SIMPLE_DATEFORMAT_ON_SECOND.format(date)).isEqualTo("2016-11-01 12:23:44");
	}

	@Test
	public void formatWithPattern() {
		Date date = new Date(116, 10, 1, 12, 23, 44);
		assertThat(DateFormatUtil.formatDate(DateFormatUtil.PATTERN_SIMPLE, date)).isEqualTo("2016-11-01 12:23:44.000");
		assertThat(DateFormatUtil.formatDate(DateFormatUtil.PATTERN_SIMPLE, date.getTime()))
				.isEqualTo("2016-11-01 12:23:44.000");
	}
	
	@Test
	public void parseWithPattern() throws ParseException {
		Date date = new Date(116, 10, 1, 12, 23, 44);
		Date resultDate = DateFormatUtil.pareDate(DateFormatUtil.PATTERN_SIMPLE, "2016-11-01 12:23:44.000");
		assertThat(resultDate.getTime()==date.getTime()).isTrue();
	}
	
	
}
