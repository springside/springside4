package org.springside.modules.utils.reflect;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentMap;

import org.springside.modules.utils.base.ExceptionUtil;
import org.springside.modules.utils.collection.MapUtil;
import org.springside.modules.utils.collection.MapUtil.ValueCreator;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

/**
 * 基于Cglib, 基于字节码生成的快速反射, 比JDK反射的速度快.
 * 
 * 比如反射调用A类的“hello” 方法，cglib会直接生成一个调用a.hello()的FastMethod子类.
 * 
 * 注意: 需要参考本模块的POM文件，显式引用cglib.
 * 
 * @author calvin
 */
public class FastMethodInvoker {

	// 存放cglib的FastClass
	private static ConcurrentMap<Class<?>, FastClass> fastClassMap = MapUtil.newConcurrentHashMap();

	private final FastMethod fastMethod;

	protected FastMethodInvoker(FastMethod fastMethod) {
		this.fastMethod = fastMethod;
	}

	/**
	 * 获取cglib生成的FastMethod，创建方法的FastMethodInvoker实例.
	 */
	public static FastMethodInvoker create(final Class<?> clz, final String methodName, Class<?>... parameterTypes) {
		Method method = ClassUtil.getAccessibleMethod(clz, methodName, parameterTypes);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + clz + ']');
		}
		return build(clz, method);
	}

	/**
	 * 获取cglib生成的FastMethod，创建Getter方法的FastMethodInvoker实例.
	 */
	public static FastMethodInvoker createGetter(final Class<?> clz, final String propertyName) {
		Method method = ClassUtil.getGetterMethod(clz, propertyName);
		if (method == null) {
			throw new IllegalArgumentException(
					"Could not find getter method [" + propertyName + "] on target [" + clz + ']');
		}
		return build(clz, method);
	}

	/**
	 * 获取cglib生成的FastMethod，创建Setter方法的FastMethodInvoker实例.
	 */
	public static FastMethodInvoker createSetter(final Class<?> clz, final String propertyName,
			Class<?> parameterType) {
		Method method = ClassUtil.getSetterMethod(clz, propertyName, parameterType);
		if (method == null) {
			throw new IllegalArgumentException(
					"Could not find getter method [" + propertyName + "] on target [" + clz + ']');
		}
		return build(clz, method);
	}

	private static FastMethodInvoker build(final Class<?> clz, Method method) {
		FastClass fastClz = MapUtil.createIfAbsent(fastClassMap, clz, new ValueCreator<FastClass>() {
			@Override
			public FastClass get() {
				return FastClass.create(clz);
			}
		});

		return new FastMethodInvoker(fastClz.getMethod(method));
	}

	/**
	 * 调用方法
	 */
	@SuppressWarnings("unchecked")
	public <T> T invoke(Object obj, Object... args) {
		try {
			return (T) fastMethod.invoke(obj, args);
		} catch (Exception e) {
			throw ExceptionUtil.uncheckedAndWrap(e);
		}
	}
}
