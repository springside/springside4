package org.springside.modules.utils.text;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class CsvUtilTest {

	@Test
	public void toCsvString() {
		assertThat(CsvUtil.toCsvString(1, 2)).isEqualTo("1,2");
	}

}
