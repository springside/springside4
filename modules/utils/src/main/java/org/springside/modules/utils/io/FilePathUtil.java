package org.springside.modules.utils.io;

import org.apache.commons.lang3.StringUtils;
import org.springside.modules.utils.base.Platforms;
import org.springside.modules.utils.text.MoreStringUtil;

import com.google.common.io.Files;

/**
 * 关于文件路径的工具集
 * 
 * @author calvin
 */
public class FilePathUtil {

	/**
	 * 在Windows环境里，兼容Windows上的路径分割符，将 '/' 转回 '\'
	 */
	public static String normalizePath(String path) {
		if (Platforms.FILE_PATH_SEPARATOR_CHAR == Platforms.WINDOWS_FILE_PATH_SEPARATOR_CHAR
				&& StringUtils.indexOf(path, Platforms.LINUX_FILE_PATH_SEPARATOR_CHAR) != -1) {
			return StringUtils.replaceChars(path, Platforms.LINUX_FILE_PATH_SEPARATOR_CHAR,
					Platforms.WINDOWS_FILE_PATH_SEPARATOR_CHAR);
		}
		return path;

	}

	/**
	 * 将路径整理，如 "a/../b"，整理成 "b"
	 */
	public static String simplifyPath(String path) {
		return Files.simplifyPath(path);
	}

	/**
	 * 以拼接路径名
	 */
	public static String contact(String baseName, String... appendName) {
		if (appendName.length == 0) {
			return baseName;
		}

		String contactName;
		if (MoreStringUtil.endWith(baseName, Platforms.FILE_PATH_SEPARATOR_CHAR)) {
			contactName = baseName + appendName[0];
		} else {
			contactName = baseName + Platforms.FILE_PATH_SEPARATOR_CHAR + appendName[0];
		}

		if (appendName.length > 1) {
			for (int i = 1; i < appendName.length; i++) {
				contactName += Platforms.FILE_PATH_SEPARATOR_CHAR + appendName[i];
			}
		}

		return contactName;
	}

	/**
	 * 获得上层目录的路径
	 */
	public static String getParentPath(String path) {
		String parentPath = path;

		if (Platforms.FILE_PATH_SEPARATOR.equals(parentPath)) {
			return parentPath;
		}

		parentPath = MoreStringUtil.removeEnd(parentPath, Platforms.FILE_PATH_SEPARATOR_CHAR);

		int idx = parentPath.lastIndexOf(Platforms.FILE_PATH_SEPARATOR_CHAR);
		if (idx >= 0) {
			parentPath = parentPath.substring(0, idx + 1);
		} else {
			parentPath = Platforms.FILE_PATH_SEPARATOR;
		}

		return parentPath;
	}

	/**
	 * 获得参数clazz所在的Jar文件的绝对路径
	 */
	public static String getJarPath(Class<?> clazz) {
		return clazz.getProtectionDomain().getCodeSource().getLocation().getFile();
	}
}
