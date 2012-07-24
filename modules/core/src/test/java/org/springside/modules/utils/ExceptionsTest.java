package org.springside.modules.utils;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class ExceptionsTest {

	@Test
	public void unchecked() {
		Exception exception = new Exception("my exception");
		RuntimeException runtimeException = Exceptions.unchecked(exception);
		assertEquals(exception, runtimeException.getCause());

		RuntimeException runtimeException2 = Exceptions.unchecked(runtimeException);
		assertEquals(runtimeException, runtimeException2);
	}

	@Test
	public void getStackTraceAsString() {
		Exception exception = new Exception("my exception");
		RuntimeException runtimeException = new RuntimeException(exception);

		String stack = Exceptions.getStackTraceAsString(runtimeException);
		System.out.println(stack);
	}

	@Test
	public void isCausedBy() {
		IOException ioexception = new IOException("my exception");
		IllegalStateException illegalStateException = new IllegalStateException(ioexception);
		RuntimeException runtimeException = new RuntimeException(illegalStateException);

		assertTrue(Exceptions.isCausedBy(runtimeException, IOException.class));
		assertTrue(Exceptions.isCausedBy(runtimeException, IllegalStateException.class, IOException.class));
		assertTrue(Exceptions.isCausedBy(runtimeException, Exception.class));
		assertFalse(Exceptions.isCausedBy(runtimeException, IllegalAccessException.class));

	}

}
