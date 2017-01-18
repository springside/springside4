package org.springside.modules.utils.number;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class UnitConverterTest {
	
	@Test
	public void convertDurationMillis(){
		assertThat(UnitConverter.convertDurationMillis("12345")).isEqualTo(12345);
		assertThat(UnitConverter.convertDurationMillis("12S")).isEqualTo(12000);
		assertThat(UnitConverter.convertDurationMillis("12s")).isEqualTo(12000);
		assertThat(UnitConverter.convertDurationMillis("12ms")).isEqualTo(12);
		assertThat(UnitConverter.convertDurationMillis("12m")).isEqualTo(12*60*1000);
	}

}
