/*******************************************************************************
 * Copyright (c) 2005, 2017 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.utils.base;

import java.lang.reflect.UndeclaredThrowableException;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.google.common.base.Throwables;

/**
 * 关于异常的工具类.
 * 
 * 1. 若干常用函数.
 * 
 * 2. StackTrace性能优化相关，尽量使用静态异常避免异常生成时获取StackTrace，及打印StackTrace的消耗
 * 
 * @author calvin
 */
public class Exceptions {

	private static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];

	/**
	 * 将CheckedException转换为RuntimeException重新抛出, 可以减少函数签名中的CheckExcetpion定义.
	 * 
	 * CheckedException会用UndeclaredThrowableException包裹，RunTimeException和Error则不会被转变.
	 * 
	 * from Commons Lange 3.5 ExceptionUtils, 但依赖包可能没有这么新，因此复制下来.
	 * 
	 * @see ExceptionUtils#wrapAndThrow(Throwable)
	 */
	public static RuntimeException unchecked(Throwable t) {
		if (t instanceof RuntimeException) {
			throw (RuntimeException) t;
		}
		if (t instanceof Error) {
			throw (Error) t;
		}
		throw new UndeclaredThrowableException(t);
	}

	/**
	 * 如果是著名的包裹类，从cause中获得真正异常. 其他异常则不变.
	 * 
	 * Future中使用的ExecutionException 与 反射时定义的InvocationTargetException， 真正的异常都封装在Cause中
	 * 
	 * 前面 unchecked() 使用的UndeclaredThrowableException同理.
	 * 
	 * from Quasar and Tomcat's ExceptionUtils
	 */
	public static Throwable unwrap(Throwable t) {
		if (t instanceof java.util.concurrent.ExecutionException
				|| t instanceof java.lang.reflect.InvocationTargetException
				|| t instanceof java.lang.reflect.UndeclaredThrowableException) {
			return t.getCause();
		}
		return t;
	}

	/**
	 * 将StackTrace[]转换为String, 供Logger或e.printStackTrace()外的其他地方使用.
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
	 * 拼装异常类名与异常信息
	 * 
	 * @see ExceptionUtils#getMessage(Throwable)
	 */
	public static String getMessageWithExceptionName(Throwable t) {
		return ExceptionUtils.getMessage(t);
	}

	/////////// StackTrace 性能优化相关////////

	/**
	 * from Netty, 为静态异常设置StackTrace.
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
		return exception;//NOSONAR
	}

	/**
	 * 清除StackTrace. 假设StackTrace已生成, 但把它打印出来也有不小的消耗. 如果不能控制打印端(如logger)，可用此方法暴力清除Trace.
	 */
	public static <T extends Throwable> T clearStackTrace(T exception) {
		exception.setStackTrace(EMPTY_STACK_TRACE);
		return exception;//NOSONAR
	}

	/**
	 * 适用于Message经常变更的异常, 可通过clone()不经过构造函数的构造异常再设定新的异常信息
	 */
	public static class CloneableException extends Exception implements Cloneable {

		private static final long serialVersionUID = -6270471689928560417L;
		protected String message;

		public CloneableException() {
			super();
		}

		public CloneableException(String message) {
			super();
			this.message = message;
		}

		public CloneableException(String message, Throwable cause) {
			super(cause);
			this.message = message;
		}

		@Override
		public CloneableException clone() {
			try {
				return (CloneableException) super.clone();
			} catch (CloneNotSupportedException e) {
				return null;//NOSONAR
			}
		}

		public CloneableException clone(String message) {
			CloneableException newException = this.clone();
			newException.setMessage(message);
			return newException;
		}

		@Override
		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
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
	public static class CloneableRuntimeException extends RuntimeException implements Cloneable {

		private static final long serialVersionUID = 3984796576627959400L;

		protected String message;

		public CloneableRuntimeException() {
			super();
		}

		public CloneableRuntimeException(String message) {
			super();
			this.message = message;
		}

		public CloneableRuntimeException(String message, Throwable cause) {
			super(cause);
			this.message = message;
		}

		@Override
		public CloneableRuntimeException clone() {
			try {
				return (CloneableRuntimeException) super.clone();
			} catch (CloneNotSupportedException e) {
				return null;//NOSONAR
			}
		}

		public CloneableRuntimeException clone(String message) {
			CloneableRuntimeException newException = this.clone();
			newException.setMessage(message);
			return newException;
		}

		@Override
		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public Throwable setStackTrace(Class<?> throwClazz, String throwMethod) {
			Exceptions.setStackTrace(this, throwClazz, throwMethod);
			return this;
		}
	}
}
