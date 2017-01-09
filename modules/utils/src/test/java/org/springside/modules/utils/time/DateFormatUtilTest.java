package org.springside.modules.utils.time;

import static org.assertj.core.api.Assertions.*;

import java.text.ParseException;
import java.util.Date;

import org.junit.Test;

public class DateFormatUtilTest {

	@Test
	public void isoDateFormat() {
		Date date = new Date(116, 10, 1, 12, 23, 44);
		assertThat(DateFormatUtil.ISO_FORMAT.format(date)).isEqualTo("2016-11-01T12:23:44.000+08:00");
		assertThat(DateFormatUtil.ISO_WITH_SECOND_FORMAT.format(date)).isEqualTo("2016-11-01T12:23:44+08:00");
		assertThat(DateFormatUtil.ISO_WITH_DATE_FORMAT.format(date)).isEqualTo("2016-11-01");
	}

	@Test
	public void simpleDateFormat() {
		Date date = new Date(116, 10, 1, 12, 23, 44);
		assertThat(DateFormatUtil.SIMPLE_FORMAT.format(date)).isEqualTo("2016-11-01 12:23:44.000");
		assertThat(DateFormatUtil.SIMPLE_WITH_SECOND_FORMAT.format(date)).isEqualTo("2016-11-01 12:23:44");
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
		assertThat(resultDate.getTime() == date.getTime()).isTrue();
	}

	public void formatDuration() {
		Date date = new Date(116, 10, 1, 12, 23, 44);
		date.setTime(date.getTime() + 100);
		//DateFormatUtils.format(date, new Date()).equalTo
	}

}
