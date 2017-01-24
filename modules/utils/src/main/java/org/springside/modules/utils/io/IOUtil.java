package org.springside.modules.utils.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.utils.io.type.StringBuilderWriter;
import org.springside.modules.utils.text.Charsets;

/**
 * IO Stream/Reader相关工具集
 * 
 * 建议使用Apache Commons IO, 在未引入Commons IO时可以用本类做最基本的事情.
 * 
 * 代码基本从Apache Commmons IO中化简移植, 固定encoding为UTF8.
 * 
 * 1. 安静关闭Closeable对象
 * 
 * 2. 读出InputStream/Reader内容到String 或 List<String>(from Commons IO)
 * 
 * 3. 将String写到OutputStream/Writer(from Commons IO)
 * 
 * 4. InputStream/Reader与OutputStream/Writer之间复制的copy(from Commons IO)
 * 
 * 
 * @author calvin
 */
public class IOUtil {
	private static final Logger logger = LoggerFactory.getLogger(IOUtil.class);

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	private static final int EOF = -1;

	private static final String CLOSE_ERROR_MESSAGE = "IOException thrown while closing Closeable.";

	/**
	 * 在final中安静的关闭, 不再往外抛出异常避免影响原有异常，最常用函数. 同时兼容Closeable为空未实际创建的情况.
	 */
	public static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				logger.warn(CLOSE_ERROR_MESSAGE, e);
			}
		}
	}

	/**
	 * 简单读取InputStream到String.
	 */
	public static String toString(InputStream input) throws IOException {
		InputStreamReader reader = new InputStreamReader(input, Charsets.UTF_8);
		return toString(reader);
	}

	/**
	 * 简单读取Reader到String
	 */
	public static String toString(Reader input) throws IOException {
		final BufferedReader reader = toBufferedReader(input);
		StringBuilderWriter sw = new StringBuilderWriter();
		copy(reader, sw);
		return sw.toString();
	}

	/**
	 * 简单读取Reader的每行内容到List<String>
	 */
	public static List<String> toLines(final InputStream input) throws IOException {
		return toLines(new InputStreamReader(input, Charsets.UTF_8));
	}

	/**
	 * 简单读取Reader的每行内容到List<String>
	 */
	public static List<String> toLines(final Reader input) throws IOException {
		final BufferedReader reader = toBufferedReader(input);
		final List<String> list = new ArrayList<String>();
		String line = reader.readLine();
		while (line != null) {
			list.add(line);
			line = reader.readLine();
		}
		return list;
	}

	/**
	 * 简单写入String到OutputStream.
	 */
	public static void write(final String data, final OutputStream output) throws IOException {
		if (data != null) {
			output.write(data.getBytes(Charsets.UTF_8));
		}
	}

	/**
	 * 简单写入String到Writer.
	 */
	public static void write(final String data, final Writer output) throws IOException {
		if (data != null) {
			output.write(data);
		}
	}

	/**
	 * 在Reader与Writer间复制内容
	 */
	public static long copy(final Reader input, final Writer output) throws IOException {
		final char[] buffer = new char[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n;
		while (EOF != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * 在InputStream与OutputStream间复制内容
	 */
	public static long copy(final InputStream input, final OutputStream output) throws IOException {

		final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n;
		while (EOF != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static BufferedReader toBufferedReader(final Reader reader) {
		return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
	}
}
