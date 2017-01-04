package org.springside.modules.utils.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
public class FileUtil {

	/**
	 * 读取文件到String.
	 */
	public static String toString(File file) throws IOException {
		return Files.toString(file, Charsets.UTF_8);
	}

	/**
	 * 读取文件的每行内容到List<String>
	 */
	public static List<String> readLines(final File file) throws IOException {
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
	 * 在临时目录创建临时目录，命名为${毫秒级时间戳}-${同一毫秒内的计数器}, from guava
	 * 
	 * @see Files#createTempDir()
	 */
	public static File createTempDir() {
		return Files.createTempDir();
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

	/**
	 * 获取文件名的扩展名部分
	 */
	public static String getFileExtension(String fullName) {
		return Files.getFileExtension(fullName);
	}
	
	/**
	 * 将路径整理，如 "a/../b"，整理成 "b"
	 */
	public static String simplifyPath(String pathname){
		return Files.simplifyPath(pathname);
	}
}
