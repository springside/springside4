package org.springside.modules.utils.reflect;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;

/**
 * 关于Class的工具类
 * 
 * 1. 封装Commons Utils的常用函数
 * 
 * @author calvin
 */
public class Classes {

	public static String getShortClassName(final Class<?> cls) {
		return ClassUtils.getShortClassName(cls);
	}

	public static String getShortClassName(final String className) {
		return ClassUtils.getShortClassName(className);
	}

	public static String getPackageName(final Class<?> cls) {
		return ClassUtils.getPackageName(cls);
	}

	public static String getPackageName(final String className) {
		return ClassUtils.getPackageName(className);
	}

	/**
	 * 递归返回所有的SupperClasses，比Spring中的相同实现靠谱
	 */
	public static List<Class<?>> getAllSuperclasses(final Class<?> cls) {
		return ClassUtils.getAllSuperclasses(cls);
	}

	/**
	 * 递归返回本类及所有基类继承的接口，比Spring中的相同实现靠谱
	 */
	public static List<Class<?>> getAllInterfaces(final Class<?> cls) {
		return ClassUtils.getAllInterfaces(cls);
	}

	public static Set<Annotation> getAllAnnotations(final Class<?> cls) {
		List<Class<?>> allTypes = getAllSuperclasses(cls);
		allTypes.addAll(getAllInterfaces(cls));
		allTypes.add(cls);
		
		Set<Annotation> anns = new HashSet<Annotation>();
		for (Class<?> type : allTypes) {
			anns.addAll(Arrays.asList(type.getDeclaredAnnotations()));
		}

		Set<Annotation> superAnnotations = new HashSet<Annotation>();
		for (Annotation ann : anns) {
			getSupperAnnotations(ann.annotationType(), superAnnotations);
		}

		anns.addAll(superAnnotations);

		return anns;
	}

	private static <A extends Annotation> void getSupperAnnotations(Class<A> annotationType, Set<Annotation> visited) {
		Annotation[] anns = annotationType.getDeclaredAnnotations();

		for (Annotation ann : anns) {
			if (!ann.annotationType().getName().startsWith("java.lang") && visited.add(ann)) {
				getSupperAnnotations(ann.annotationType(), visited);
			}
		}
	}

}
