package org.springside.modules.utils.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.springside.modules.utils.text.Charsets;

import com.google.common.io.Files;

/**
 * 关于文件的工具集
 * 
 * 代码基本从调用Guava Files, 固定encoding为UTF8.
 * 
 * @author calvin
 */
public abstract class FileUtil {

	/**
	 * 打开文件为InputStream
	 */
	public static InputStream asStream(String fileName) throws IOException {
		return new FileInputStream(new File(fileName));
	}

	/**
	 * 读取文件到byte[].
	 */
	public static byte[] toByteArray(File file) throws IOException {
		return Files.toByteArray(file);
	}

	/**
	 * 读取文件到String.
	 */
	public static String toString(File file) throws IOException {
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
	public static void append(CharSequence from, File to, Charset charset) throws IOException {
		Files.append(from, to, Charsets.UTF_8);
	}

	/**
	 * 文件复制
	 */
	public static void copy(File from, File to) throws IOException {
		Files.copy(from, to);
	}

	/**
	 * 文件移动
	 */
	public static void move(File from, File to) throws IOException {
		Files.move(from, to);
	}

	/**
	 * 创建文件或更新时间戳
	 */
	public static void touch(File file) throws IOException {
		Files.touch(file);
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
	 * 在临时目录创建临时文件，命名为${prefix}${random.nextLong()}${suffix}
	 */
	public static File createTempFile(String prefix, String suffix) throws IOException {
		return File.createTempFile(prefix, suffix);
	}

	/**
	 * 确保父目录及其父目录直到根目录都已经创建.
	 */
	public static void createParentDirs(File file) throws IOException {
		Files.createParentDirs(file);
	}

	/**
	 * 获取File的BufferedReader
	 */
	public static BufferedReader newReader(File file) throws FileNotFoundException {
		return Files.newReader(file, Charsets.UTF_8);
	}

	/**
	 * 获取File的BufferedWriter
	 */
	public static BufferedWriter newWriter(File file, Charset charset) throws FileNotFoundException {
		return Files.newWriter(file, Charsets.UTF_8);
	}

}
