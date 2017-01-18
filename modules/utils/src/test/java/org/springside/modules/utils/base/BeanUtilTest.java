package org.springside.modules.utils.base;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class BeanUtilTest {
	
	@Test
	public void test(){
		assertThat(BooleanUtil.parseGeneralString("1",false)).isFalse();
		assertThat(BooleanUtil.parseGeneralString("y",false)).isTrue();
	}

}
