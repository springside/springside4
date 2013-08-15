package org.springside.examples.showcase.demos.utilities.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.io.output.WriterOutputStream;
import org.junit.Test;
import org.springside.modules.utils.Exceptions;

import com.google.common.base.Charsets;

/**
 * 演示各种IO操作.
 * 
 * @author calvin
 */
public class IODemo {

	@Test
	public void workWithFileContent() {
		File file = new File("woop.txt");
		File destFile = new File("bar.txt");
		try {
			// text -> file, 类似的还有Collection<String>, byte[] ->file
			FileUtils.writeStringToFile(file, "Hey sailor!\nHaha\n", "UTF-8");

			// inputstream -> file，
			InputStream source = IOUtils.toInputStream("Hej", "UTF-8");
			FileUtils.copyInputStreamToFile(source, file);

			// ///////////////////////////
			// file -> outputstream
			System.out.println("copy File to outputstream:");
			FileUtils.copyFile(file, System.out);

			// file -> file
			FileUtils.copyFile(file, destFile);

			// file -> string
			System.out.println("File to String:");
			System.out.println(FileUtils.readFileToString(file, "UTF-8"));

			// file -> list<string>
			System.out.println("File to List<String>:");
			List<String> lines = FileUtils.readLines(file, "UTF-8");
			for (String string : lines) {
				System.out.println(string);
			}
		} catch (IOException e) {
			Exceptions.unchecked(e);
		}
	}

	/*
	 * String/Input/OutputStream/Reader/Writer
	 */
	@Test
	public void workWithStream() {
		InputStream in = null;
		try {
			String content = "Stream testing";

			// String - > InputStream.
			in = IOUtils.toInputStream(content, "UTF-8");

			// String - > OutputStream
			System.out.println("String to OutputStram:");
			IOUtils.write(content, System.out, "UTF-8");

			// //////////////////
			// InputStream/Reader -> String
			System.out.println("\nInputStram to String:");
			System.out.println(IOUtils.toString(in, "UTF-8"));

			// InputStream/Reader -> OutputStream/Writer， 四者间可任意组合.
			InputStream in2 = IOUtils.toInputStream(content); // 重新准备inputSteam
			System.out.println("InputStream to OutputStream:");
			IOUtils.copy(in2, System.out);

			// /////////////////
			// InputStream ->Reader
			InputStreamReader reader = new InputStreamReader(in, Charsets.UTF_8);
			// Reader->InputStream
			ReaderInputStream in3 = new ReaderInputStream(reader, Charsets.UTF_8);

			// OutputStream ->Writer
			OutputStreamWriter writer = new OutputStreamWriter(System.out, Charsets.UTF_8);
			// Writer->OutputStream
			WriterOutputStream out2 = new WriterOutputStream(writer, Charsets.UTF_8);

			// ////////////////////
			// 收集Writer的输出内容到String.
			StringWriter sw = new StringWriter();
			sw.write("I am String writer");
			System.out.println("\nCollect writer content:");
			System.out.println(sw.toString());

		} catch (IOException e) {
			Exceptions.unchecked(e);
		} finally {
			// 安静的关闭Stream
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void workWithFileAndDir() {
		// 看FileUtils的JavaDoc即可
	}
}
