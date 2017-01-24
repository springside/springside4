package org.springside.modules.utils.reflect;

import java.lang.reflect.Method;

import org.springside.modules.utils.base.ExceptionUtil;

public class MethodInvoker {

	private final Method method;

	public static MethodInvoker createMethod(final Class<?> clz, final String methodName, Class<?>... parameterTypes) {
		Method method = ClassUtil.getAccessibleMethod(clz, methodName, parameterTypes);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + clz + ']');
		}
		return new MethodInvoker(method);
	}

	public static MethodInvoker createGetter(final Class<?> clz, final String propertyName) {
		Method method = ClassUtil.getGetterMethod(clz, propertyName);
		if (method == null) {
			throw new IllegalArgumentException(
					"Could not find getter method [" + propertyName + "] on target [" + clz + ']');
		}
		return new MethodInvoker(method);
	}

	public static MethodInvoker createSetter(final Class<?> clz, final String propertyName, Class<?> parameterType) {
		Method method = ClassUtil.getSetterMethod(clz, propertyName, parameterType);
		if (method == null) {
			throw new IllegalArgumentException(
					"Could not find getter method [" + propertyName + "] on target [" + clz + ']');
		}
		return new MethodInvoker(method);
	}

	protected MethodInvoker(Method method) {
		this.method = method;
	}

	/**
	 * 调用已准备好的Method
	 */
	public <T> T invoke(final Object obj, Object... args) {
		try {
			return (T) method.invoke(obj, args);
		} catch (Exception e) {
			throw ExceptionUtil.uncheckedAndWrap(e);
		}
	}

}
