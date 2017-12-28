package org.springside.modules.utils.base;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springside.modules.utils.base.annotation.Nullable;

import com.google.common.base.Throwables;

/**
 * 关于异常的工具类.
 * 
 * 1. Checked/Uncheked及Wrap(如ExecutionException)的转换.
 * 
 * 2. 打印Exception的辅助函数.
 * 
 * 3. StackTrace性能优化相关，尽量使用静态异常避免异常生成时获取StackTrace，及打印StackTrace的消耗
 */
public class ExceptionUtil {

	private static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];

	///// Checked/Uncheked及Wrap(如ExecutionException)的转换/////

	/**
	 * 将CheckedException转换为RuntimeException重新抛出, 可以减少函数签名中的CheckExcetpion定义.
	 * 
	 * CheckedException会用UndeclaredThrowableException包裹，RunTimeException和Error则不会被转变.
	 * 
	 * from Commons Lange 3.5 ExceptionUtils.
	 * 
	 * 虽然unchecked()里已直接抛出异常，但仍然定义返回值，方便欺骗Sonar。因此本函数也改变了一下返回值
	 * 
	 * 示例代码:
	 * 
	 * <pre>
	 * try{ ... }catch(Exception e){ throw unchecked(t); }
	 * </pre>
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

		throw new UncheckedException(t);
	}

	/**
	 * 如果是著名的包裹类，从cause中获得真正异常. 其他异常则不变.
	 * 
	 * Future中使用的ExecutionException 与 反射时定义的InvocationTargetException， 真正的异常都封装在Cause中
	 * 
	 * 前面 unchecked() 使用的UncheckedException同理.
	 * 
	 * from Quasar and Tomcat's ExceptionUtils
	 */
	public static Throwable unwrap(Throwable t) {
		if (t instanceof java.util.concurrent.ExecutionException
				|| t instanceof java.lang.reflect.InvocationTargetException || t instanceof UncheckedException) {
			return t.getCause();
		}

		return t;
	}

	/**
	 * 组合unchecked与unwrap的效果
	 */
	public static RuntimeException uncheckedAndWrap(Throwable t) {

		Throwable unwrapped = unwrap(t);
		if (unwrapped instanceof RuntimeException) {
			throw (RuntimeException) unwrapped;
		}
		if (unwrapped instanceof Error) {
			throw (Error) unwrapped;
		}

		throw new UncheckedException(unwrapped);
	}

	/**
	 * 自定义一个CheckedException的wrapper.
	 * 
	 * 返回Message/Cause时, 将返回内层Exception的信息.
	 */
	public static class UncheckedException extends RuntimeException {

		private static final long serialVersionUID = 4140223302171577501L;

		public UncheckedException(Throwable cause) {
			super(cause);
		}

		@Override
		public String getMessage() {
			return super.getCause().getMessage();
		}
	}

	////// 输出内容相关 //////

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
	 * 如无底层Cause, 则返回自身
	 * 
	 * @see Throwables#getRootCause(Throwable)
	 */
	public static Throwable getRootCause(Throwable t) {
		return Throwables.getRootCause(t);
	}

	/**
	 * 判断异常是否由某些底层的异常引起.
	 */
	@SuppressWarnings("unchecked")
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
	 * 拼装 短异常类名: 异常信息.
	 * 
	 * 与Throwable.toString()相比使用了短类名
	 * 
	 * @see ExceptionUtils#getMessage(Throwable)
	 */
	public static String toStringWithShortName(@Nullable Throwable t) {
		return ExceptionUtils.getMessage(t);
	}

	/**
	 * 拼装 短异常类名: 异常信息 <-- RootCause的短异常类名: 异常信息
	 */
	public static String toStringWithRootCause(@Nullable Throwable t) {
		if (t == null) {
			return StringUtils.EMPTY;
		}

		final String clsName = ClassUtils.getShortClassName(t, null);
		final String message = StringUtils.defaultString(t.getMessage());
		Throwable cause = getRootCause(t);

		StringBuilder sb = new StringBuilder(128).append(clsName).append(": ").append(message);
		if (cause != t) { // NOSONAR
			sb.append("; <---").append(toStringWithShortName(cause));
		}

		return sb.toString();
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
	 * private static RuntimeException TIMEOUT_EXCEPTION = ExceptionUtil.setStackTrace(new RuntimeException("Timeout"),
	 * 		MyClass.class, "mymethod");
	 * </pre>
	 */
	public static <T extends Throwable> T setStackTrace(T exception, Class<?> throwClass, String throwClazz) {
		exception.setStackTrace(
				new StackTraceElement[] { new StackTraceElement(throwClass.getName(), throwClazz, null, -1) });
		return exception;// NOSONAR
	}

	/**
	 * 清除StackTrace. 假设StackTrace已生成, 但把它打印出来也有不小的消耗.
	 * 
	 * 如果不能控制StackTrace的生成，也不能控制它的打印端(如logger)，可用此方法暴力清除Trace.
	 * 
	 * 但Cause链依然不能清除, 只能清除每一个Cause的StackTrace.
	 */
	public static <T extends Throwable> T clearStackTrace(T exception) {
		Throwable cause = exception;
		while (cause != null) {
			cause.setStackTrace(EMPTY_STACK_TRACE);
			cause = cause.getCause();
		}
		return exception;// NOSONAR
	}

	/**
	 * 适用于异常信息需要变更的情况, 可通过clone()，不经过构造函数（也就避免了获得StackTrace）地从之前定义的静态异常中克隆，再设定新的异常信息
	 * 
	 * private static CloneableException TIMEOUT_EXCEPTION = new CloneableException("Timeout") .setStackTrace(My.class,
	 * "hello"); ...
	 * 
	 * throw TIMEOUT_EXCEPTION.clone("Timeout for 40ms");
	 * 
	 */
	public static class CloneableException extends Exception implements Cloneable {

		private static final long serialVersionUID = -6270471689928560417L;
		protected String message;

		public CloneableException() {
			super((Throwable) null);
		}

		public CloneableException(String message) {
			super((Throwable) null);
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
			} catch (CloneNotSupportedException e) {// NOSONAR
				return null;
			}
		}

		@Override
		public String getMessage() {
			return message;
		}

		/**
		 * 简便函数，定义静态异常时使用
		 */
		public CloneableException setStackTrace(Class<?> throwClazz, String throwMethod) {
			ExceptionUtil.setStackTrace(this, throwClazz, throwMethod);
			return this;
		}

		/**
		 * 简便函数, clone并重新设定Message
		 */
		public CloneableException clone(String message) {
			CloneableException newException = this.clone();
			newException.setMessage(message);
			return newException;
		}

		/**
		 * 简便函数, 重新设定Message
		 */
		public CloneableException setMessage(String message) {
			this.message = message;
			return this;
		}
	}

	/**
	 * 适用于异常信息需要变更的情况, 可通过clone()，不经过构造函数（也就避免了获得StackTrace）地从之前定义的静态异常中克隆，再设定新的异常信息
	 * 
	 * @see CloneableException
	 */
	public static class CloneableRuntimeException extends RuntimeException implements Cloneable {

		private static final long serialVersionUID = 3984796576627959400L;

		protected String message;

		public CloneableRuntimeException() {
			super((Throwable) null);
		}

		public CloneableRuntimeException(String message) {
			super((Throwable) null);
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
			} catch (CloneNotSupportedException e) { // NOSONAR
				return null;
			}
		}

		@Override
		public String getMessage() {
			return message;
		}

		/**
		 * 简便函数，定义静态异常时使用
		 */
		public CloneableRuntimeException setStackTrace(Class<?> throwClazz, String throwMethod) {
			ExceptionUtil.setStackTrace(this, throwClazz, throwMethod);
			return this;
		}

		/**
		 * 简便函数, clone并重新设定Message
		 */
		public CloneableRuntimeException clone(String message) {
			CloneableRuntimeException newException = this.clone();
			newException.setMessage(message);
			return newException;
		}

		/**
		 * 简便函数, 重新设定Message
		 */
		public CloneableRuntimeException setMessage(String message) {
			this.message = message;
			return this;
		}
	}
}
