package org.springside.modules.utils.io;

import java.io.File;

import org.apache.commons.lang3.Validate;
import org.springside.modules.utils.base.Platforms;

import com.google.common.io.Files;

/**
 * 关于文件名，文件路径的工具集
 * 
 * @author calvin
 */
public abstract class FilePathUtil {

	/**
	 * 获取文件名(不包含路径)
	 */
	public static String getName(String fullName) {
		Validate.notEmpty(fullName);
		int last = fullName.lastIndexOf(Platforms.FILE_PATH_SEPARATOR_CHAR);
		return fullName.substring(last + 1);
	}

	/**
	 * 获取文件名的扩展名部分
	 */
	public static String getFileExtension(String fullName) {
		return Files.getFileExtension(fullName);
	}

	/**
	 * 将路径整理，如 "a/../b"，整理成 "b"
	 */
	public static String simplifyPath(String pathName) {
		return Files.simplifyPath(pathName);
	}

	/**
	 * 拼接路径名
	 */
	public static String contactName(String baseName, String appendName) {
		return baseName + Platforms.FILE_PATH_SEPARATOR_CHAR + appendName;
	}

	/**
	 * from Jodd
	 */
	public static boolean isExistingFile(File file) {
		if (file == null) {
			return false;
		}
		return file.exists() && file.isFile();
	}

	/**
	 * from Jodd
	 */
	public static boolean isExistingFolder(File folder) {
		if (folder == null) {
			return false;
		}
		return folder.exists() && folder.isDirectory();
	}
}
