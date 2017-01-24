package org.springside.modules.utils.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springside.modules.utils.base.Platforms;
import org.springside.modules.utils.base.annotation.NotNull;
import org.springside.modules.utils.base.annotation.Nullable;
import org.springside.modules.utils.text.Charsets;

import com.google.common.io.Files;

/**
 * 关于文件的工具集
 * 
 * 代码基本从调用Guava Files, 固定encoding为UTF8.
 * 
 * 1.文件读写
 * 
 * 2.文件及目录操作
 * 
 * @author calvin
 */
public class FileUtil {
	
	

	//////// 文件读写//////

	/**
	 * 读取文件到byte[].
	 */
	public static byte[] toByteArray(final File file) throws IOException {
		return Files.toByteArray(file);
	}

	/**
	 * 读取文件到String.
	 */
	public static String toString(final File file) throws IOException {
		return Files.toString(file, Charsets.UTF_8);
	}

	/**
	 * 读取文件的每行内容到List<String>
	 */
	public static List<String> toLines(final File file) throws IOException {
		return Files.readLines(file, Charsets.UTF_8);
	}

	/**
	 * 简单写入String到File.
	 */
	public static void write(final CharSequence data, final File file) throws IOException {
		Files.write(data, file, Charsets.UTF_8);
	}

	/**
	 * 追加String到File.
	 */
	public static void append(final CharSequence from, final File to) throws IOException {
		Files.append(from, to, Charsets.UTF_8);
	}

	/**
	 * 打开文件为InputStream
	 */
	public static InputStream asInputStream(String fileName) throws IOException {
		return new FileInputStream(getFileByPath(fileName));
	}
	
	/**
	 * 打开文件为InputStream
	 */
	public static InputStream asInputStream(File file) throws IOException {
		return new FileInputStream(file);
	}

	/**
	 * 打开文件为OutputStream
	 */
	public static OutputStream asOututStream(String fileName) throws IOException {
		return new FileOutputStream(getFileByPath(fileName));
	}
	
	/**
	 * 打开文件为OutputStream
	 */
	public static OutputStream asOututStream(File file) throws IOException {
		return new FileOutputStream(file);
	}

	/**
	 * 获取File的BufferedReader
	 */
	public static BufferedReader asBufferedReader(String fileName) throws FileNotFoundException {
		return Files.newReader(getFileByPath(fileName), Charsets.UTF_8);
	}

	/**
	 * 获取File的BufferedWriter
	 */
	public static BufferedWriter asBufferedWriter(String fileName) throws FileNotFoundException {
		return Files.newWriter(getFileByPath(fileName), Charsets.UTF_8);
	}

	///// 文件操作 /////

	/**
	 * 复制文件或目录
	 * 
	 * @param from 如果为null，或者是不存在的文件或目录，抛出异常.
	 * @param to 如果为null，或者from是目录而to是已存在文件，或相反
	 */
	public static void copy(@NotNull File from, @NotNull File to) throws IOException {
		Validate.notNull(from);
		Validate.notNull(to);

		if (from.isDirectory()) {
			copyDir(from, to);
		} else {
			copyFile(from, to);
		}
	}

	/**
	 * 文件复制.
	 * 
	 * @param from 如果为nll，或文件不存在或者是目录，，抛出异常
	 * @param to 如果to为null，或文件存在但是一个目录，抛出异常
	 */
	public static void copyFile(@NotNull File from, @NotNull File to) throws IOException {
		Validate.isTrue(isFileExists(from), from + " is not exist or not a file");
		Validate.notNull(to);
		Validate.isTrue(!FileUtil.isDirExists(to), to + " is exist but it is a dir");
		Files.copy(from, to);
	}

	/**
	 * 复制目录
	 */
	public static void copyDir(@NotNull File from, @NotNull File to) throws IOException {
		Validate.isTrue(isDirExists(from), from + " is not exist or not a dir");
		Validate.notNull(to);

		if (to.exists()) {
			Validate.isTrue(!to.isFile(), to + " is exist but it is a file");
		} else {
			to.mkdirs();
		}

		File[] files = from.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				String name = files[i].getName();
				if (".".equals(name) || "..".equals(name)) {
					continue;
				}
				copy(files[i], new File(to, name));
			}
		}
	}

	/**
	 * 文件移动/重命名.
	 */
	public static void moveFile(@NotNull File from, @NotNull File to) throws IOException {
		Validate.isTrue(isFileExists(from), from + " is not exist or not a file");
		Validate.notNull(to);
		Validate.isTrue(!isDirExists(to), to + " is  exist but it is a dir");

		Files.move(from, to);
	}

	/**
	 * 目录移动/重命名
	 */
	public static void moveDir(@NotNull File from, @NotNull File to) throws IOException {
		Validate.isTrue(isDirExists(from), from + " is not exist or not a dir");
		Validate.notNull(to);
		Validate.isTrue(!isFileExists(to), to + " is exist but it is a file");

		final boolean rename = from.renameTo(to);
		if (!rename) {
			if (to.getCanonicalPath().startsWith(from.getCanonicalPath() + File.separator)) {
				throw new IOException("Cannot move directory: " + from + " to a subdirectory of itself: " + to);
			}
			copyDir(from, to);
			deleteDir(from);
			if (from.exists()) {
				throw new IOException("Failed to delete original directory '" + from + "' after copy to '" + to + '\'');
			}
		}
	}

	/**
	 * 创建文件或更新时间戳.
	 */
	public static void touch(String filePath) throws IOException {
		Files.touch(getFileByPath(filePath));
	}

	/**
	 * 创建文件或更新时间戳.
	 */
	public static void touch(File file) throws IOException {
		Files.touch(file);
	}

	/**
	 * 删除文件.
	 * 
	 * 如果文件不存在或者是目录，则不做修改
	 */
	public static void deleteFile(@Nullable File file) throws IOException {
		Validate.isTrue(isFileExists(file), file + " is not exist or not a file");
		file.delete();
	}

	/**
	 * 删除目录及所有子目录/文件
	 */
	public static void deleteDir(File dir) {
		Validate.isTrue(isDirExists(dir), dir + " is not exist or not a dir");

		// 后序遍历，先删掉子目录中的文件/目录
		Iterator<File> iterator = Files.fileTreeTraverser().postOrderTraversal(dir).iterator();
		while (iterator.hasNext()) {
			iterator.next().delete();
		}
	}

	/**
	 * 判断目录是否存在, from Jodd
	 */
	public static boolean isDirExists(String dirPath) {
		return isDirExists(getFileByPath(dirPath));
	}

	/**
	 * 判断目录是否存在, from Jodd
	 */
	public static boolean isDirExists(File dir) {
		if (dir == null) {
			return false;
		}
		return dir.exists() && dir.isDirectory();
	}

	/**
	 * 确保目录存在, 如不存在则创建
	 */
	public static void makesureDirExists(String dirPath) throws IOException {
		makesureDirExists(getFileByPath(dirPath));
	}

	/**
	 * 确保目录存在, 如不存在则创建
	 */
	public static void makesureDirExists(File file) throws IOException {
		Validate.notNull(file);
		if (file.exists()) {
			if (!file.isDirectory()) {
				throw new IOException("There is a file exists " + file);
			}
		} else {
			file.mkdirs();
		}
	}

	/**
	 * 确保父目录及其父目录直到根目录都已经创建.
	 * 
	 * @see Files#createParentDirs(File)
	 */
	public static void makesureParentDirExists(File file) throws IOException {
		Files.createParentDirs(file);
	}

	/**
	 * 判断文件是否存在, from Jodd
	 */
	public static boolean isFileExists(String fileName) {
		return isFileExists(getFileByPath(fileName));
	}

	/**
	 * 判断文件是否存在, from Jodd
	 */
	public static boolean isFileExists(File file) {
		if (file == null) {
			return false;
		}
		return file.exists() && file.isFile();
	}

	/**
	 * 在临时目录创建临时目录，命名为${毫秒级时间戳}-${同一毫秒内的计数器}, from guava
	 * 
	 * @see Files#createTempDir()
	 */
	public static File createTempDir() {
		return Files.createTempDir();
	}

	/**
	 * 在临时目录创建临时文件，命名为tmp-${random.nextLong()}.tmp
	 */
	public static File createTempFile() throws IOException {
		return File.createTempFile("tmp-", ".tmp");
	}

	/**
	 * 在临时目录创建临时文件，命名为${prefix}${random.nextLong()}${suffix}
	 */
	public static File createTempFile(String prefix, String suffix) throws IOException {
		return File.createTempFile(prefix, suffix);
	}

	private static File getFileByPath(String filePath) {
		return StringUtils.isBlank(filePath) ? null : new File(filePath);
	}

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
}
