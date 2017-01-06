package org.springside.modules.utils.time;

import static org.assertj.core.api.Assertions.*;

import java.util.Date;

import org.junit.Test;

public class CachingDatFormatterTest {

	@Test
	public void test(){
		Date date = new Date(116, 10, 1, 12, 23, 44);
		
		CachingDateFormatter formatter = new CachingDateFormatter(DateFormatUtil.PATTERN_SIMPLE);
		assertThat(formatter.format(date.getTime())).isEqualTo("2016-11-01 12:23:44.000");
		assertThat(formatter.format(date.getTime())).isEqualTo("2016-11-01 12:23:44.000");
		assertThat(formatter.format(date.getTime()+2)).isEqualTo("2016-11-01 12:23:44.002");
	}
}
