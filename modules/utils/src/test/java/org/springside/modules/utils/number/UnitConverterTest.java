package org.springside.modules.utils.number;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class UnitConverterTest {

	@Test
	public void convertDurationMillis() {
		assertThat(UnitConverter.convertDurationMillis("12345")).isEqualTo(12345);
		assertThat(UnitConverter.convertDurationMillis("12S")).isEqualTo(12000);
		assertThat(UnitConverter.convertDurationMillis("12s")).isEqualTo(12000);
		assertThat(UnitConverter.convertDurationMillis("12ms")).isEqualTo(12);
		assertThat(UnitConverter.convertDurationMillis("12m")).isEqualTo(12 * 60 * 1000);
		assertThat(UnitConverter.convertDurationMillis("12h")).isEqualTo(12l * 60 * 60 * 1000);
		assertThat(UnitConverter.convertDurationMillis("12d")).isEqualTo(12l * 24 * 60 * 60 * 1000);

		try {
			assertThat(UnitConverter.convertDurationMillis("12a")).isEqualTo(12 * 60 * 1000);
			fail("should fail");
		} catch (Throwable t) {
			assertThat(t).isInstanceOf(IllegalArgumentException.class);
		}
		
		try {
			assertThat(UnitConverter.convertDurationMillis("a12")).isEqualTo(12 * 60 * 1000);
			fail("should fail");
		} catch (Throwable t) {
			assertThat(t).isInstanceOf(IllegalArgumentException.class);
		}
	}

	@Test
	public void convertSizeBytes() {
		assertThat(UnitConverter.convertSizeBytes("12345")).isEqualTo(12345);
		assertThat(UnitConverter.convertSizeBytes("12b")).isEqualTo(12);
		assertThat(UnitConverter.convertSizeBytes("12k")).isEqualTo(12 * 1024);
		assertThat(UnitConverter.convertSizeBytes("12M")).isEqualTo(12 * 1024 * 1024);

		assertThat(UnitConverter.convertSizeBytes("12G")).isEqualTo(12l * 1024 * 1024 * 1024);
		assertThat(UnitConverter.convertSizeBytes("12T")).isEqualTo(12l * 1024 * 1024 * 1024 * 1024);

		try {
			UnitConverter.convertSizeBytes("12x");
			fail("should fail");
		} catch (Throwable t) {
			assertThat(t).isInstanceOf(IllegalArgumentException.class);
		}
		
		try {
			UnitConverter.convertSizeBytes("a12");
			fail("should fail");
		} catch (Throwable t) {
			assertThat(t).isInstanceOf(IllegalArgumentException.class);
		}
	}

}
