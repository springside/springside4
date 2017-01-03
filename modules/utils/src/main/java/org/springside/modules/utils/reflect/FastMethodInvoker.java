package org.springside.modules.utils.reflect;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentMap;

import org.springside.modules.utils.collection.Maps;
import org.springside.modules.utils.collection.Maps.ValueCreator;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

/**
 * 基于Cglib, 基于字节码生成的快速反射.
 * 
 * @author calvin
 */
public class FastMethodInvoker {

	private static ConcurrentMap<Class<?>, FastClass> classMap = Maps.newConcurrentHashMap();

	/**
	 * 获取cglib生成的FastMethod
	 */
	public static FastMethod getFastMethod(final Object obj, final String methodName, Class<?>... parameterTypes) {

		final Class<?> clz = obj.getClass();
		FastClass fastClz = Maps.createIfAbsent(classMap, clz, new ValueCreator<FastClass>() {
			@Override
			public FastClass get() {
				return FastClass.create(clz);
			}
		});

		Method method = Reflections.getAccessibleMethod(obj, methodName, parameterTypes);
		return fastClz.getMethod(method);
	}

}
