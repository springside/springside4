package org.springside.modules.utils.reflect;

import com.google.common.annotations.Beta;

/**
 * Class加载相关的工具类
 * 
 * @author calvin
 */
@Beta
public class ClassLoaderUtil {

	private static final String CLASS_FILE_NAME_EXTENSION = ".class";

	public static boolean isClassFile(String resourceName) {
		return resourceName.endsWith(CLASS_FILE_NAME_EXTENSION);
	}

	/**
	 * 返回Class所在的Jar包的文件路径.
	 */
	public static String getJarFile(Class<?> clazz) {
		return clazz.getProtectionDomain().getCodeSource().getLocation().getFile();
	}

	/**
	 * 从类名转换为路径名, 如org.spring.Hello 转为 org/spring/Hello.class
	 */
	public static String getClassFileName(String className) {
		return className.replace('.', '/') + CLASS_FILE_NAME_EXTENSION;
	}

	/**
	 * 从类转换为路径名, 如org.spring.Hello 转为 org/spring/Hello.class
	 */
	public static String getClassFileName(Class<?> clazz) {
		if (clazz.isArray()) {
			clazz = clazz.getComponentType();
		}
		return getClassFileName(clazz.getName());
	}
}
