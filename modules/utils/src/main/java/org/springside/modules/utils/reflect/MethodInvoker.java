package org.springside.modules.utils.reflect;

import java.lang.reflect.Method;

import org.springside.modules.utils.base.ExceptionUtil;

public class MethodInvoker {

	private final Method method;

	public static MethodInvoker createMethod(final Class<?> clz, final String methodName, Class<?>... parameterTypes) {
		Method method = ClassUtil.getAccessibleMethod(clz, methodName, parameterTypes);
		return new MethodInvoker(method);
	}

	public static MethodInvoker createGetter(final Class<?> clz, final String propertyName) {
		Method method = ClassUtil.getGetterMethod(clz, propertyName);
		return new MethodInvoker(method);
	}

	public static MethodInvoker createSetter(final Class<?> clz, final String propertyName, Class<?> parameterType) {
		Method method = ClassUtil.getSetterMethod(clz, propertyName, parameterType);
		return new MethodInvoker(method);
	}

	protected MethodInvoker(Method method) {
		this.method = method;
	}

	/**
	 * 调用已准备好的Method
	 */
	public <T> T invokeMethod(final Object obj, Object... args) {
		try {
			return (T) method.invoke(obj, args);
		} catch (Exception e) {
			throw ExceptionUtil.unchecked(ExceptionUtil.unwrap(e));
		}
	}

}
