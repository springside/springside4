/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.utils.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.springside.modules.utils.base.ExceptionUtil;
import org.springside.modules.utils.base.ExceptionUtil.UncheckedException;

/**
 * 反射工具类.
 * 
 * 所有反射均无视modifier的范围限制，同时将反射的Checked异常转为UnChecked异常。
 * 
 * 1. 对于构造函数，直接使用本类即可
 * 
 * 2. 对于方法调用，本类全部是一次性调用的简化方法，如果考虑性能，对反复调用的方法应使用 MethodInvoker 及 FastMethodInvoker.
 * 
 * 3. 对于直接属性访问，恰当使用本类中的一次性调用，和基于预先获取的Field对象反复调用两种做法.
 */
@SuppressWarnings("unchecked")
public class ReflectionUtil {

	/////////// 属性相关函数 ///////////
	/**
	 * 调用Getter方法, 无视private/protected修饰符.
	 */
	public static <T> T invokeGetter(Object obj, String propertyName) {
		Method method = ClassUtil.getGetterMethod(obj.getClass(), propertyName);
		if (method == null) {
			throw new IllegalArgumentException(
					"Could not find getter method [" + propertyName + "] on target [" + obj + ']');
		}
		return (T) invokeMethod(obj, method);
	}

	/**
	 * 调用Setter方法, 无视private/protected修饰符, 按传入value的类型匹配函数.
	 */
	public static void invokeSetter(Object obj, String propertyName, Object value) {
		Method method = ClassUtil.getSetterMethod(obj.getClass(), propertyName, value.getClass());
		if (method == null) {
			throw new IllegalArgumentException(
					"Could not find getter method [" + propertyName + "] on target [" + obj + ']');
		}
		invokeMethod(obj, method, value);
	}

	/**
	 * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
	 */
	public static <T> T getFieldValue(final Object obj, final String fieldName) {
		Field field = ClassUtil.getAccessibleField(obj.getClass(), fieldName);
		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + ']');
		}
		return getFieldValue(obj, field);
	}

	/**
	 * 使用已获取的Field, 直接读取对象属性值, 不经过getter函数.
	 */
	public static <T> T getFieldValue(final Object obj, final Field field) {
		try {
			return (T) field.get(obj);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	/**
	 * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
	 */
	public static void setFieldValue(final Object obj, final String fieldName, final Object value) {
		Field field = ClassUtil.getAccessibleField(obj.getClass(), fieldName);
		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + ']');
		}
		setField(obj, field, value);
	}

	/**
	 * 使用已获取的Field, 直接读取对象属性值, 不经过setter函数.
	 */
	public static void setField(final Object obj, Field field, final Object value) {
		try {
			field.set(obj, value);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	/**
	 * 先尝试用Getter函数读取, 如果不存在则直接读取变量.
	 */
	public static <T> T getProperty(Object obj, String propertyName) {
		Method method = ClassUtil.getGetterMethod(obj.getClass(), propertyName);
		if (method != null) {
			try {
				return (T) method.invoke(obj, ArrayUtils.EMPTY_OBJECT_ARRAY);
			} catch (Exception e) {
				throw convertReflectionExceptionToUnchecked(e);
			}
		} else {
			return (T) getFieldValue(obj, propertyName);
		}
	}

	/**
	 * 先尝试用Setter函数写入, 如果不存在则直接写入变量, 按传入value的类型匹配函数.
	 */
	public static void setProperty(Object obj, String propertyName, final Object value) {
		Method method = ClassUtil.getSetterMethod(obj.getClass(), propertyName, value.getClass());
		if (method != null) {
			try {
				method.invoke(obj, value);
			} catch (Exception e) {
				throw convertReflectionExceptionToUnchecked(e);
			}
		} else {
			setFieldValue(obj, propertyName, value);
		}
	}

	/////////// 方法相关函数 ////////////
	/**
	 * 直接调用对象方法, 无视private/protected修饰符.
	 * 
	 * 根据传入参数的实际类型进行匹配
	 */
	public static <T> T invokeMethod(Object obj, String methodName, Object... args) {
		Object[] theArgs = ArrayUtils.nullToEmpty(args);
		final Class<?>[] parameterTypes = ClassUtils.toClass(theArgs);
		return (T) invokeMethod(obj, methodName, theArgs, parameterTypes);
	}

	/**
	 * 直接调用对象方法, 无视private/protected修饰符.
	 * 
	 * 根据定义的参数类型进行匹配
	 */
	public static <T> T invokeMethod(final Object obj, final String methodName, final Object[] args,
			final Class<?>[] parameterTypes) {
		Method method = ClassUtil.getAccessibleMethod(obj.getClass(), methodName, parameterTypes);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + ']');
		}
		return invokeMethod(obj, method, args);
	}

	/**
	 * 直接调用对象方法, 无视private/protected修饰符
	 * 
	 * 只匹配函数名，如果有多个同名函数调用第一个. 用于确信只有一个同名函数, 但参数类型不确定的情况
	 */
	public static <T> T invokeMethodByName(final Object obj, final String methodName, final Object[] args) {
		Method method = ClassUtil.getAccessibleMethodByName(obj.getClass(), methodName);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + ']');
		}
		return invokeMethod(obj, method, args);
	}

	/**
	 * 调用已准备好的Method
	 */
	public static <T> T invokeMethod(final Object obj, Method method, Object... args) {
		try {
			return (T) method.invoke(obj, args);
		} catch (Exception e) {
			throw ExceptionUtil.uncheckedAndWrap(e);
		}
	}

	////////// 构造函数 ////////
	/**
	 * 调用构造函数.
	 */
	public static <T> T invokeConstructor(final Class<T> cls, Object... args) {
		try {
			return ConstructorUtils.invokeConstructor(cls, args);
		} catch (Exception e) {
			throw ExceptionUtil.uncheckedAndWrap(e);
		}
	}

	/////// 辅助函数 ////////

	/**
	 * 将反射时的checked exception转换为unchecked exception.
	 */
	public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
		if ((e instanceof IllegalAccessException) || (e instanceof NoSuchMethodException)) {
			return new IllegalArgumentException(e);
		} else if (e instanceof InvocationTargetException) {
			return new RuntimeException(((InvocationTargetException) e).getTargetException());
		} else if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		}
		return new UncheckedException(e);
	}
}
