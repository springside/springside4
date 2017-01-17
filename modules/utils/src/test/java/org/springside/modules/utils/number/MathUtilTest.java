package org.springside.modules.utils.number;

import static org.assertj.core.api.Assertions.*;

import java.math.RoundingMode;

import org.junit.Test;

public class MathUtilTest {

	@Test
	public void power2() {
		try {
			assertThat(MathUtil.nextPowerOfTwo(-5)).isEqualTo(8);
			fail("should fail here");
		} catch (Exception e) {
			assertThat(e).isInstanceOf(IllegalArgumentException.class);
		}
		assertThat(MathUtil.nextPowerOfTwo(5)).isEqualTo(8);
		assertThat(MathUtil.nextPowerOfTwo(99)).isEqualTo(128);

		assertThat(MathUtil.previousPowerOfTwo(5)).isEqualTo(4);
		assertThat(MathUtil.previousPowerOfTwo(99)).isEqualTo(64);

		assertThat(MathUtil.isPowerOfTwo(32)).isTrue();
		assertThat(MathUtil.isPowerOfTwo(31)).isFalse();

		assertThat(MathUtil.nextPowerOfTwo(5L)).isEqualTo(8L);
		assertThat(MathUtil.nextPowerOfTwo(99L)).isEqualTo(128L);

		assertThat(MathUtil.previousPowerOfTwo(5L)).isEqualTo(4L);
		assertThat(MathUtil.previousPowerOfTwo(99L)).isEqualTo(64L);

		assertThat(MathUtil.isPowerOfTwo(32L)).isTrue();
		assertThat(MathUtil.isPowerOfTwo(31L)).isFalse();
		assertThat(MathUtil.isPowerOfTwo(-2)).isFalse();

		assertThat(MathUtil.modByPowerOfTwo(0, 16)).isEqualTo(0);
		assertThat(MathUtil.modByPowerOfTwo(1, 16)).isEqualTo(1);
		assertThat(MathUtil.modByPowerOfTwo(31, 16)).isEqualTo(15);
		assertThat(MathUtil.modByPowerOfTwo(32, 16)).isEqualTo(0);
		assertThat(MathUtil.modByPowerOfTwo(-1, 16)).isEqualTo(15);

	}

	@Test
	public void gcd() {
		assertThat(MathUtil.gcd(5, 6)).isEqualTo(1);
		assertThat(MathUtil.gcd(12, 18)).isEqualTo(6);
		assertThat(MathUtil.gcd(100, 1000)).isEqualTo(100);

		assertThat(MathUtil.gcd(5L, 6L)).isEqualTo(1L);
		assertThat(MathUtil.gcd(12L, 18L)).isEqualTo(6L);
		assertThat(MathUtil.gcd(100L, 1000L)).isEqualTo(100L);
	}

	@Test
	public void cal() {
		assertThat(10 / 4).isEqualTo(2);
		assertThat(MathUtil.divide(10, 4, RoundingMode.HALF_UP)).isEqualTo(3);
	}

}
