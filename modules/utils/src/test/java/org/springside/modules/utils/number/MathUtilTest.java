package org.springside.modules.utils.number;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class MathUtilTest {
	
	@Test
	public void power2(){
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
		
		
	}

}
