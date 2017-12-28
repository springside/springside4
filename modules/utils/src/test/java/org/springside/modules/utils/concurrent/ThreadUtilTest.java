package org.springside.modules.utils.concurrent;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.springside.modules.utils.base.ObjectUtil;
import org.springside.modules.utils.base.RuntimeUtil;
public class ThreadUtilTest {
	@Test
	public void testCaller(){
		hello();
		new MyClass().hello();
		assertThat(RuntimeUtil.getCurrentClass()).isEqualTo("org.springside.modules.utils.concurrent.ThreadUtilTest");
		assertThat(RuntimeUtil.getCurrentMethod()).isEqualTo("org.springside.modules.utils.concurrent.ThreadUtilTest.testCaller()");
		
	}
	
	private void hello(){
		assertThat(RuntimeUtil.getCallerClass()).isEqualTo("org.springside.modules.utils.concurrent.ThreadUtilTest");
		assertThat(RuntimeUtil.getCallerMethod()).isEqualTo("org.springside.modules.utils.concurrent.ThreadUtilTest.testCaller()");
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		System.out.println(ObjectUtil.toPrettyString(stacktrace));
	}

	public static class MyClass{
		public void hello(){
			assertThat(RuntimeUtil.getCallerClass()).isEqualTo("org.springside.modules.utils.concurrent.ThreadUtilTest");
			assertThat(RuntimeUtil.getCallerMethod()).isEqualTo("org.springside.modules.utils.concurrent.ThreadUtilTest.testCaller()");
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
			System.out.println(ObjectUtil.toPrettyString(stacktrace));
		}
	}
}
