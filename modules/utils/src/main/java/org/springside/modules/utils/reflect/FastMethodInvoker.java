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
 * @author calvin
 */
public class FastMethodInvoker {

	// 存放cglib的FastClass
	private static ConcurrentMap<Class<?>, FastClass> fastClassMap = MapUtil.newConcurrentHashMap();

	/**
	 * 获取cglib生成的FastMethod，并保存起来留作后用
	 */
	public static FastMethod getFastMethod(final Class<?> clz, final String methodName, Class<?>... parameterTypes) {

		FastClass fastClz = MapUtil.createIfAbsent(fastClassMap, clz, new ValueCreator<FastClass>() {
			@Override
			public FastClass get() {
				return FastClass.create(clz);
			}
		});

		Method method = ReflectionUtil.getAccessibleMethod(clz, methodName, parameterTypes);

		return fastClz.getMethod(method);
	}

	/**
	 * 调用方法
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invoke(FastMethod method, Object obj, Object... args) {
		try {
			return (T) method.invoke(obj, args);
		} catch (Exception e) {
			throw ExceptionUtil.unchecked(ExceptionUtil.unwrap(e));
		}
	}

}
