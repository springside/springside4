/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.utils.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.utils.base.ExceptionUtil;

/**
 * 反射工具类.
 * 
 * 所有反射均无视modifier的范围限制，同时将反射的Checked异常转为UnChecked异常。
 * 
 * 基于这两点要求，没有现有库满足，只能自写
 * 
 * 1. 属性访问：a.调用getter/setter方法, b.直接访问变量, c.直接先尝试查找getter/setter，如果不存在则直接访问变量
 * 
 * 2. 一次性调用方法，其中一个特色函数是invokeByName，不准确匹配参数类型，只取第一个名称符合的函数.
 * 
 * 3. 多次调用方法，先用getAccessibleMethod()获得Method对象，然后多次调用Method对象
 * 
 * 4. 调用构造函数创建对象
 */
@SuppressWarnings("unchecked")
public class ReflectionUtil {
	private static final String SETTER_PREFIX = "set";

	private static final String GETTER_PREFIX = "get";

	private static Logger logger = LoggerFactory.getLogger(ReflectionUtil.class);

	/////////// 属性相关函数///////////
	/**
	 * 调用Getter方法,无视private/protected修饰符.
	 */
	public static <T> T invokeGetter(Object obj, String propertyName) {
		String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(propertyName);
		return (T) invokeMethod(obj, getterMethodName);
	}

	/**
	 * 调用Setter方法, 无视private/protected修饰符, 按传入value的类型匹配函数.
	 */
	public static void invokeSetter(Object obj, String propertyName, Object value) {
		String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(propertyName);
		invokeMethod(obj, setterMethodName, new Object[] { value });
	}

	/**
	 * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
	 */
	public static <T> T getFieldValue(final Object obj, final String fieldName) {
		Field field = getAccessibleField(obj, fieldName);

		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + ']');
		}

		Object result = null;
		try {
			result = field.get(obj);
		} catch (IllegalAccessException e) {
			logger.error("不可能抛出的异常{}", e.getMessage());
		}
		return (T) result;
	}

	/**
	 * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
	 */
	public static void setFieldValue(final Object obj, final String fieldName, final Object value) {
		Field field = getAccessibleField(obj, fieldName);

		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + ']');
		}

		try {
			field.set(obj, value);
		} catch (IllegalAccessException e) {
			logger.error("不可能抛出的异常:{}", e.getMessage());
		}
	}

	/**
	 * 先尝试用Getter函数读取, 如果不存在则直接读取变量.
	 */
	public static <T> T getProperty(Object obj, String propertyName) {
		String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(propertyName);
		Method method = getAccessibleMethod(obj, getterMethodName);
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
		String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(propertyName);
		Method method = getAccessibleMethod(obj, setterMethodName, value.getClass());
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

	////////// 方法相关函数 //////

	/**
	 * 直接调用对象方法, 无视private/protected修饰符.
	 * 
	 * 根据传入参数的实际类型进行匹配
	 * 
	 * 用于一次性调用的情况，否则应使用getAccessibleMethod()函数获得Method后反复调用.
	 */
	public static <T> T invokeMethod(Object obj, String methodName, Object... args) {
		args = ArrayUtils.nullToEmpty(args);
		final Class<?>[] parameterTypes = ClassUtils.toClass(args);
		return (T) invokeMethod(obj, methodName, args, parameterTypes);
	}

	/**
	 * 直接调用对象方法, 无视private/protected修饰符.
	 * 
	 * 根据定义的参数类型进行匹配，
	 * 
	 * 用于一次性调用的情况，否则应使用getAccessibleMethod()函数获得Method后反复调用.
	 */
	public static Object invokeMethod(final Object obj, final String methodName, final Object[] args,
			final Class<?>[] parameterTypes) {
		Method method = getAccessibleMethod(obj, methodName, parameterTypes);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + ']');
		}

		try {
			return method.invoke(obj, args);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	/**
	 * 直接调用对象方法, 无视private/protected修饰符
	 * 
	 * 只匹配函数名，如果有多个同名函数调用第一个. 用于确信只有一个同名函数, 但参数类型不好确定的情况
	 * 
	 * 用于一次性调用的情况，否则应使用getAccessibleMethodByName()函数获得Method后反复调用.
	 */
	public static Object invokeMethodByName(final Object obj, final String methodName, final Object[] args) {
		Method method = getAccessibleMethodByName(obj, methodName);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + ']');
		}

		try {
			return method.invoke(obj, args);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
	 * 
	 * 如向上转型到Object仍无法找到, 返回null.
	 * 
	 * 因为class.getFiled(); 不能获取父类的private函数, 因此采用循环向上的getDeclaredField();
	 */
	public static Field getAccessibleField(final Object obj, final String fieldName) {
		Validate.notNull(obj, "object can't be null");
		Validate.notEmpty(fieldName, "fieldName can't be blank");
		for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				Field field = superClass.getDeclaredField(fieldName);
				makeAccessible(field);
				return field;
			} catch (NoSuchFieldException e) {// NOSONAR
				// Field不在当前类定义,继续向上转型
			}
		}
		return null;
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredMethod, 并强制设置为可访问.
	 * 
	 * 如向上转型到Object仍无法找到, 返回null.
	 * 
	 * 匹配函数名+参数类型.
	 * 
	 * 用于方法需要被多次调用的情况. 先使用本函数先取得Method, 然后调用Method.invoke(Object obj, Object... args)
	 * 
	 * 因为class.getFiled() 不能获取父类的private函数, 因此采用循环向上的getDeclaredField();
	 */
	public static Method getAccessibleMethod(final Object obj, final String methodName, Class<?>... parameterTypes) {
		Validate.notNull(obj, "object can't be null");
		Validate.notEmpty(methodName, "methodName can't be blank");
		parameterTypes = ArrayUtils.nullToEmpty(parameterTypes);

		for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType
				.getSuperclass()) {
			try {
				Method method = searchType.getDeclaredMethod(methodName, parameterTypes);
				makeAccessible(method);
				return method;
			} catch (NoSuchMethodException e) {
				// Method不在当前类定义,继续向上转型
			}
		}
		return null;
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
	 * 
	 * 如向上转型到Object仍无法找到, 返回null.
	 * 
	 * 只匹配函数名, 如果有多个同名函数返回第一个
	 * 
	 * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
	 * 
	 * 因为class.getMethods() 不能获取父类的private函数, 因此采用循环向上的getMethods();
	 */
	public static Method getAccessibleMethodByName(final Object obj, final String methodName) {
		Validate.notNull(obj, "object can't be null");
		Validate.notEmpty(methodName, "methodName can't be blank");

		for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType
				.getSuperclass()) {
			Method[] methods = searchType.getDeclaredMethods();
			for (Method method : methods) {
				if (method.getName().equals(methodName)) {
					makeAccessible(method);
					return method;
				}
			}
		}
		return null;
	}

	/**
	 * 改变private/protected的方法为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
	 */
	public static void makeAccessible(Method method) {
		if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
				&& !method.isAccessible()) {
			method.setAccessible(true);
		}
	}

	/**
	 * 改变private/protected的成员变量为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
	 */
	public static void makeAccessible(Field field) {
		if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
				|| Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
			field.setAccessible(true);
		}
	}

	/**
	 * 调用构造函数.
	 */
	public static <T> T invokeConstructor(final Class<T> cls, Object... args) {
		try {
			return ConstructorUtils.invokeConstructor(cls, args);
		} catch (Exception e) {
			throw ExceptionUtil.unchecked(e);
		}
	}

	/**
	 * 调用构造函数.
	 */
	public static <T> T invokeConstructor(final Class<T> cls, Object[] args, Class<?>[] parameterTypes) {
		try {
			return ConstructorUtils.invokeConstructor(cls, args, parameterTypes);
		} catch (Exception e) {
			throw ExceptionUtil.unchecked(e);
		}
	}

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
		return new RuntimeException("Unexpected Checked Exception.", e);
	}
}
