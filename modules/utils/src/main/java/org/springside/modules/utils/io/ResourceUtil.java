package org.springside.modules.utils.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

import org.springside.modules.utils.text.Charsets;

import com.google.common.io.Resources;

/**
 * 针对Jar包内的文件的工具类
 */
public abstract class ResourceUtil {

	// 打开文件////

	public static File asFile(String resourceName) throws IOException {
		try {
			return new File(Resources.getResource(resourceName).toURI());
		} catch (URISyntaxException e) {
			return null;
		}
	}

	public static File asFile(Class<?> contextClass, String resourceName) throws IOException {
		try {
			return new File(Resources.getResource(contextClass, resourceName).toURI());
		} catch (URISyntaxException e) {
			return null;
		}
	}

	public static InputStream asStream(String resourceName) throws IOException {
		return Resources.getResource(resourceName).openStream();
	}

	public static InputStream asStream(Class<?> contextClass, String resourceName) throws IOException {
		return Resources.getResource(contextClass, resourceName).openStream();
	}

	////// 读取内容／／／／／

	public static String toString(String resourceName) throws IOException {
		return Resources.toString(Resources.getResource(resourceName), Charsets.UTF_8);
	}

	/**
	 * 根据Class的相对路径计算resource name
	 */
	public static String toString(Class<?> contextClass, String resourceName) throws IOException {
		return Resources.toString(Resources.getResource(contextClass, resourceName), Charsets.UTF_8);
	}

	public static List<String> toLines(String resourceName) throws IOException {
		return Resources.readLines(Resources.getResource(resourceName), Charsets.UTF_8);
	}

	/**
	 * 根据Class的相对路径计算resource name
	 */
	public static List<String> toLines(Class<?> contextClass, String resourceName) throws IOException {
		return Resources.readLines(Resources.getResource(contextClass, resourceName), Charsets.UTF_8);
	}
}
