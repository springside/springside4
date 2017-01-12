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
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
 * 2.文件操作
 * 
 * @author calvin
 */
public abstract class FileUtil {

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
	 * 打开文件为OutputStream
	 */
	public static OutputStream asOututStream(String fileName) throws IOException {
		return new FileOutputStream(getFileByPath(fileName));
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

	///// 文件操作/////

	/**
	 * 文件复制
	 * 
	 * 如果文件不存在，不做修改
	 */
	public static void copy(@Nullable File from, File to) throws IOException {
		if (isFileExists(from)) {
			Files.copy(from, to);
		}
	}

	/**
	 * 文件移动.
	 * 
	 * 如果文件不存在，不做修改
	 */
	public static void move(@Nullable File from, File to) throws IOException {
		if (isFileExists(from)) {
			Files.move(from, to);
		}
	}

	/**
	 * 创建文件或更新时间戳
	 */
	public static void touch(File file) throws IOException {
		Files.touch(file);
	}

	/**
	 * 删除文件
	 * 
	 * 如果文件不存在，不做修改
	 */
	public static void delete(@Nullable File file) throws IOException {
		if (isFileExists(file)) {
			file.delete();
		}
	}

	/**
	 * 确保父目录及其父目录直到根目录都已经创建.
	 */
	public static void createParentDirs(File file) throws IOException {
		Files.createParentDirs(file);
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
	 * 在临时目录创建临时文件，命名为tmp-${random.nextLong()}
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
	 * 判断目录是否存在, from Jodd
	 */
	public static boolean isFolderExists(String fileName) {
		return isFolderExists(getFileByPath(fileName));
	}

	/**
	 * 判断目录是否存在, from Jodd
	 */
	public static boolean isFolderExists(File folder) {
		if (folder == null) {
			return false;
		}
		return folder.exists() && folder.isDirectory();
	}

	private static File getFileByPath(String filePath) {
		return StringUtils.isBlank(filePath) ? null : new File(filePath);
	}
}
