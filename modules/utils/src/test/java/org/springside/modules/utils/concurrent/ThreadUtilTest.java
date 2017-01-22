package org.springside.modules.utils.concurrent;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
public class ThreadUtilTest {
	@Test
	public void testCaller(){
		hello();
	}
	
	private void hello(){
		assertThat(ThreadUtil.getCallerClass()).isEqualTo("org.springside.modules.utils.concurrent.ThreadUtilTest");
		assertThat(ThreadUtil.getCallerMethod()).isEqualTo("org.springside.modules.utils.concurrent.ThreadUtilTest.testCaller()");
		
	}

}
