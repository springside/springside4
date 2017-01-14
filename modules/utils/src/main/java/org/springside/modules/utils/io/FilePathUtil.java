package org.springside.modules.utils.io;

import java.io.File;

import org.apache.commons.lang3.Validate;
import org.springside.modules.utils.base.Platforms;
import org.springside.modules.utils.base.annotation.NotNull;
import org.springside.modules.utils.text.MoreStringUtil;

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
	public static String getFileName(@NotNull String fullName) {
		Validate.notEmpty(fullName);
		int last = fullName.lastIndexOf(Platforms.FILE_PATH_SEPARATOR_CHAR);
		return fullName.substring(last + 1);
	}

	/**
	 * 获取文件名的扩展名部分(不包含.)
	 */
	public static String getFileExtension(File file) {
		return Files.getFileExtension(file.getName());
	}

	/**
	 * 获取文件名的扩展名部分(不包含.)
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
}
