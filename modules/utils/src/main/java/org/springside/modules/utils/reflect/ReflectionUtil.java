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
import java.util.IdentityHashMap;
import java.util.Map;

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
 * 1. 属性访问，包括：a.调用getter/setter方法, b.直接访问变量, c.直接先尝试查找getter/setter，如果不存在则直接访问变量
 * 
 * 2. 一次性调用方法. 其中一个特色函数是invokeMethodByName，不准确匹配参数类型，只取第一个名称符合的函数.
 * 
 * 3. 多次调用方法，先用getAccessibleMethod()获得Method对象，然后多次调用Method对象.
 * 
 * 4. 一次性调用构造函数,创建对象
 */
@SuppressWarnings("unchecked")
public class ReflectionUtil {
	private static final String SETTER_PREFIX = "set";

	private static final String GETTER_PREFIX = "get";

	private static final String IS_PREFIX = "is";

	private static Logger logger = LoggerFactory.getLogger(ReflectionUtil.class);

	private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<Class<?>, Class<?>>(8);

	static {
		primitiveWrapperTypeMap.put(Boolean.class, Boolean.TYPE);
		primitiveWrapperTypeMap.put(Byte.class, Byte.TYPE);
		primitiveWrapperTypeMap.put(Character.class, Character.TYPE);
		primitiveWrapperTypeMap.put(Double.class, Double.TYPE);
		primitiveWrapperTypeMap.put(Float.class, Float.TYPE);
		primitiveWrapperTypeMap.put(Integer.class, Integer.TYPE);
		primitiveWrapperTypeMap.put(Long.class, Long.TYPE);
		primitiveWrapperTypeMap.put(Short.class, Short.TYPE);
	}

	/////////// 属性相关函数, 用于一次性调用的情况 ///////////
	/**
	 * 调用Getter方法, 无视private/protected修饰符.
	 */
	public static <T> T invokeGetter(Object obj, String propertyName) {
		String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(propertyName);

		Method method = getAccessibleMethod(obj.getClass(), getterMethodName);

		// retry on another name
		if (method == null) {
			getterMethodName = IS_PREFIX + StringUtils.capitalize(propertyName);
			method = getAccessibleMethod(obj.getClass(), getterMethodName);
		}

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
		String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(propertyName);
		invokeMethod(obj, setterMethodName, new Object[] { value });
	}

	/**
	 * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
	 */
	public static <T> T getFieldValue(final Object obj, final String fieldName) {
		Field field = getAccessibleField(obj.getClass(), fieldName);

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
		Field field = getAccessibleField(obj.getClass(), fieldName);

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
		Method method = getAccessibleMethod(obj.getClass(), getterMethodName);
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
		Method method = getAccessibleMethod(obj.getClass(), setterMethodName, value.getClass());
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
		Object[] theArgs = ArrayUtils.nullToEmpty(args);
		final Class<?>[] parameterTypes = ClassUtils.toClass(theArgs);
		return (T) invokeMethod(obj, methodName, theArgs, parameterTypes);
	}

	/**
	 * 直接调用对象方法, 无视private/protected修饰符.
	 * 
	 * 根据定义的参数类型进行匹配，
	 * 
	 * 用于一次性调用的情况，否则应使用getAccessibleMethod()函数获得Method后反复调用.
	 */
	public static <T> T invokeMethod(final Object obj, final String methodName, final Object[] args,
			final Class<?>[] parameterTypes) {
		Method method = getAccessibleMethod(obj.getClass(), methodName, parameterTypes);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + ']');
		}

		try {
			return (T) method.invoke(obj, args);
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
	public static <T> T invokeMethodByName(final Object obj, final String methodName, final Object[] args) {
		Method method = findAccessibleMethodByName(obj.getClass(), methodName);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + ']');
		}

		try {
			return (T) method.invoke(obj, args);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	/**
	 * 调用已准备好的Method
	 */
	public static <T> T invokeMethod(final Object obj, Method method, Object... args) {
		try {
			return (T) method.invoke(obj, args);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	//////

	/**
	 * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
	 * 
	 * 如向上转型到Object仍无法找到, 返回null.
	 * 
	 * 因为class.getFiled(); 不能获取父类的private函数, 因此采用循环向上的getDeclaredField();
	 */
	public static Field getAccessibleField(final Class clazz, final String fieldName) {
		Validate.notNull(clazz, "clazz can't be null");
		Validate.notEmpty(fieldName, "fieldName can't be blank");
		for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
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
	public static Method getAccessibleMethod(final Class clazz, final String methodName, Class<?>... parameterTypes) {
		Validate.notNull(clazz, "class can't be null");
		Validate.notEmpty(methodName, "methodName can't be blank");
		Class[] theParameterTypes = ArrayUtils.nullToEmpty(parameterTypes);

		// 处理原子类型与对象类型的兼容
		wrapClassses(theParameterTypes);

		for (Class<?> searchType = clazz; searchType != Object.class; searchType = searchType.getSuperclass()) {
			try {
				Method method = searchType.getDeclaredMethod(methodName, theParameterTypes);
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
	public static Method findAccessibleMethodByName(final Class clazz, final String methodName) {
		Validate.notNull(clazz, "clazz can't be null");
		Validate.notEmpty(methodName, "methodName can't be blank");

		for (Class<?> searchType = clazz; searchType != Object.class; searchType = searchType.getSuperclass()) {
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

	/**
	 * 兼容原子类型与非原子类型的转换，不考虑依赖两者不同来区分不同函数的场景
	 */
	private static void wrapClassses(Class<?>[] source) {
		for (int i = 0; i < source.length; i++) {
			Class<?> wrapClass = primitiveWrapperTypeMap.get(source[i]);
			if (wrapClass != null) {
				source[i] = wrapClass;
			}
		}
	}

}
