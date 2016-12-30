/*******************************************************************************
 * Copyright (c) 2005, 2017 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.utils.base;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.google.common.base.Throwables;

/**
 * 关于异常的工具类.
 */
public class Exceptions {

	private static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];

	/**
	 * 将CheckedException转换为RuntimeException, Error则不变. 可减少CheckExcetpion造成的函数定义污染.
	 */
	public static RuntimeException unchecked(Throwable t) {
		if (t instanceof RuntimeException)
			throw ((RuntimeException) t);
		if (t instanceof Error)
			throw ((Error) t);
		else
			throw new RuntimeException(t);
	}

	/**
	 * 参考Netty, 为静态异常设置StackTrace.
	 * 
	 * 对某些已知且经常抛出的异常, 不需要每次创建异常类并很消耗性能的并生成完整的StackTrace. 此时可使用静态声明的异常.
	 * 
	 * 如果异常可能在多个地方抛出，使用本函数设置抛出的类名和方法名.
	 * 
	 * <pre>
	 * private static TimeoutException TIMEOUT_EXCEPTION = staticStackTrace(new TimeOutException(), MyClass.class,
	 * 		"hello");
	 * </pre>
	 */
	public static <T extends Throwable> T setStackTrace(T exception, Class<?> throwClass, String throwClazz) {
		exception.setStackTrace(
				new StackTraceElement[] { new StackTraceElement(throwClass.getName(), throwClazz, null, -1) });
		return exception;
	}

	/**
	 * 清除StackTrace
	 */
	public static <T extends Throwable> T clearStackTrace(T exception) {
		exception.setStackTrace(EMPTY_STACK_TRACE);
		return exception;
	}

	/**
	 * 将StackTrace[]转换为String.
	 * 
	 * @see Throwables#getStackTraceAsString(Throwable)
	 */
	public static String stackTraceText(Throwable t) {
		return Throwables.getStackTraceAsString(t);
	}

	/**
	 * 获取异常的Root Cause.
	 * 
	 * @see Throwables#getRootCause(Throwable)
	 */
	public static Throwable getRootCause(Throwable t) {
		return Throwables.getRootCause(t);
	}

	/**
	 * 判断异常是否由某些底层的异常引起.
	 */
	public static boolean isCausedBy(Throwable t, Class<? extends Exception>... causeExceptionClasses) {
		Throwable cause = t;

		while (cause != null) {
			for (Class<? extends Exception> causeClass : causeExceptionClasses) {
				if (causeClass.isInstance(cause)) {
					return true;
				}
			}
			cause = cause.getCause();
		}
		return false;
	}

	/**
	 * 拼装异常类名与异常信息的信息
	 * 
	 * @see ExceptionUtils#getMessage(Throwable)
	 */
	public static String getMessageWithExceptionName(Throwable t) {
		return ExceptionUtils.getMessage(t);
	}

	/**
	 * 重载fillInStackTrace()方法，不生成StackTrace. 
	 * 
	 * 适用于Message经常变更，不能使用静态异常时.
	 */
	public static class EmptyStackStraceException extends Exception {

		private static final long serialVersionUID = -6270471689928560417L;

		public EmptyStackStraceException(String message) {
			super(message);
		}

		public EmptyStackStraceException(String message, Throwable cause) {
			super(message, cause);
		}

		@Override
		public Throwable fillInStackTrace() {
			return this;
		}

		public Throwable setStackTrace(Class<?> throwClazz, String throwMethod) {
			Exceptions.setStackTrace(this, throwClazz, throwMethod);
			return this;
		}
	}

	/**
	 * 重载fillInStackTrace()方法，不生成StackTrace.
	 * 
	 * 适用于Message经常变更，不能使用静态异常时
	 */
	public static class EmptyStackStraceRunTimeException extends RuntimeException {

		private static final long serialVersionUID = 4702426016231026020L;

		public EmptyStackStraceRunTimeException(String message) {
			super(message);
		}

		public EmptyStackStraceRunTimeException(String message, Throwable cause) {
			super(message, cause);
		}

		@Override
		public Throwable fillInStackTrace() {
			return this;
		}

		public Throwable setStackTrace(Class<?> throwClazz, String throwMethod) {
			Exceptions.setStackTrace(this, throwClazz, throwMethod);
			return this;
		}
	}
}
