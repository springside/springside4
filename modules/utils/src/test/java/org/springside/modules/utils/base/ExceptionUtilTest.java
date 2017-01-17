/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.utils.base;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.springside.modules.utils.base.ExceptionUtil.CloneableException;
import org.springside.modules.utils.base.ExceptionUtil.CloneableRuntimeException;

public class ExceptionUtilTest {

	private static RuntimeException TIMEOUT_EXCEPTION = ExceptionUtil.setStackTrace(new RuntimeException("Timeout"),
			ExceptionUtilTest.class, "hello");

	private static CloneableException TIMEOUT_EXCEPTION2 = new CloneableException("Timeout")
			.setStackTrace(ExceptionUtilTest.class, "hello");

	private static CloneableRuntimeException TIMEOUT_EXCEPTION3 = new CloneableRuntimeException("Timeout")
			.setStackTrace(ExceptionUtilTest.class, "hello");

	@Test
	public void unchecked() {
		// convert Exception to RuntimeException with cause
		Exception exception = new Exception("my exception");
		try {
			ExceptionUtil.unchecked(exception);
			fail("should fail before");
		} catch (Throwable t) {
			assertThat(t.getCause()).isSameAs(exception);
		}

		// do nothing of RuntimeException
		RuntimeException runtimeException = new RuntimeException("haha");
		try {
			ExceptionUtil.unchecked(runtimeException);
			fail("should fail before");
		} catch (Throwable t) {
			assertThat(t).isSameAs(runtimeException);
		}

	}

	@Test
	public void unwrap() {
		RuntimeException re = new RuntimeException("my runtime");
		assertThat(ExceptionUtil.unwrap(re)).isSameAs(re);

		ExecutionException ee = new ExecutionException(re);
		assertThat(ExceptionUtil.unwrap(ee)).isSameAs(re);

		InvocationTargetException ie = new InvocationTargetException(re);
		assertThat(ExceptionUtil.unwrap(ie)).isSameAs(re);
	}

	@Test
	public void getStackTraceAsString() {
		Exception exception = new Exception("my exception");
		RuntimeException runtimeException = new RuntimeException(exception);

		String stack = ExceptionUtil.stackTraceText(runtimeException);
		System.out.println(stack);
	}

	@Test
	public void isCausedBy() {
		IOException ioexception = new IOException("my exception");
		IllegalStateException illegalStateException = new IllegalStateException(ioexception);
		RuntimeException runtimeException = new RuntimeException(illegalStateException);

		assertThat(ExceptionUtil.isCausedBy(runtimeException, IOException.class)).isTrue();
		assertThat(ExceptionUtil.isCausedBy(runtimeException, IllegalStateException.class, IOException.class)).isTrue();
		assertThat(ExceptionUtil.isCausedBy(runtimeException, Exception.class)).isTrue();
		assertThat(ExceptionUtil.isCausedBy(runtimeException, IllegalAccessException.class)).isFalse();
	}

	@Test
	public void getRootCause() {
		IOException ioexception = new IOException("my exception");
		IllegalStateException illegalStateException = new IllegalStateException(ioexception);
		RuntimeException runtimeException = new RuntimeException(illegalStateException);

		assertThat(ExceptionUtil.getRootCause(runtimeException)).isSameAs(ioexception);
	}

	@Test
	public void getName() {
		IOException ioexception = new IOException("my exception");
		assertThat(ExceptionUtil.getMessageWithExceptionName(ioexception)).isEqualTo("IOException: my exception");
	}

	@Test
	public void clearStackTrace() {
		IOException ioexception = new IOException("my exception");
		RuntimeException runtimeException = new RuntimeException(ioexception);

		System.out.println(ExceptionUtil.stackTraceText(ExceptionUtil.clearStackTrace(runtimeException)));

	}

	@Test
	public void staticException() {
		assertThat(ExceptionUtil.stackTraceText(TIMEOUT_EXCEPTION)).hasLineCount(2)
				.contains("java.lang.RuntimeException: Timeout")
				.contains("at org.springside.modules.utils.base.ExceptionUtilTest.hello(Unknown Source)");

		assertThat(ExceptionUtil.stackTraceText(TIMEOUT_EXCEPTION2)).hasLineCount(2)
				.contains("org.springside.modules.utils.base.ExceptionUtil$CloneableException: Timeout")
				.contains("at org.springside.modules.utils.base.ExceptionUtilTest.hello(Unknown Source)");

		CloneableException timeoutException = TIMEOUT_EXCEPTION2.clone("Timeout for 30ms");
		assertThat(ExceptionUtil.stackTraceText(timeoutException)).hasLineCount(2)
				.contains("org.springside.modules.utils.base.ExceptionUtil$CloneableException: Timeout for 30ms")
				.contains("at org.springside.modules.utils.base.ExceptionUtilTest.hello(Unknown Source)");

		assertThat(ExceptionUtil.stackTraceText(TIMEOUT_EXCEPTION3)).hasLineCount(2)
				.contains("org.springside.modules.utils.base.ExceptionUtil$CloneableRuntimeException: Timeout")
				.contains("at org.springside.modules.utils.base.ExceptionUtilTest.hello(Unknown Source)");

		CloneableRuntimeException timeoutRuntimeException = TIMEOUT_EXCEPTION3.clone("Timeout for 40ms");
		assertThat(ExceptionUtil.stackTraceText(timeoutRuntimeException)).hasLineCount(2)
				.contains("org.springside.modules.utils.base.ExceptionUtil$CloneableRuntimeException: Timeout for 40ms")
				.contains("at org.springside.modules.utils.base.ExceptionUtilTest.hello(Unknown Source)");

	}

}
