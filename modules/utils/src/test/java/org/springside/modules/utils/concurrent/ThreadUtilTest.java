package org.springside.modules.utils.concurrent;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.springside.modules.utils.base.ObjectUtil;
public class ThreadUtilTest {
	@Test
	public void testCaller(){
		hello();
		new MyClass().hello();
		assertThat(ThreadUtil.getCurrentClass()).isEqualTo("org.springside.modules.utils.concurrent.ThreadUtilTest");
		assertThat(ThreadUtil.getCurrentMethod()).isEqualTo("org.springside.modules.utils.concurrent.ThreadUtilTest.testCaller()");
		
	}
	
	private void hello(){
		assertThat(ThreadUtil.getCallerClass()).isEqualTo("org.springside.modules.utils.concurrent.ThreadUtilTest");
		assertThat(ThreadUtil.getCallerMethod()).isEqualTo("org.springside.modules.utils.concurrent.ThreadUtilTest.testCaller()");
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		System.out.println(ObjectUtil.toPrettyString(stacktrace));
	}

	public static class MyClass{
		public void hello(){
			assertThat(ThreadUtil.getCallerClass()).isEqualTo("org.springside.modules.utils.concurrent.ThreadUtilTest");
			assertThat(ThreadUtil.getCallerMethod()).isEqualTo("org.springside.modules.utils.concurrent.ThreadUtilTest.testCaller()");
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
			System.out.println(ObjectUtil.toPrettyString(stacktrace));
		}
	}
}
