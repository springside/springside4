package org.springside.modules.utils.io;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class FilePathUtilTest {

	@Test
	public void getName() {
		assertThat(FilePathUtil.getFileName("/a/d/b/abc.txt")).isEqualTo("abc.txt");
		assertThat(FilePathUtil.getFileName("abc.txt")).isEqualTo("abc.txt");

		assertThat(FilePathUtil.getFileExtension("/a/d/b/abc.txt")).isEqualTo("txt");
		assertThat(FilePathUtil.getFileExtension("/a/d/b/abc")).isEqualTo("");
		assertThat(FilePathUtil.getFileExtension("/a/d/b/abc.")).isEqualTo("");

	}

	@Test
	public void pathName() {
		String filePath = FilePathUtil.contact("/abc/", "ef");
		assertThat(filePath).isEqualTo("/abc/ef");

		String filePath2 = FilePathUtil.contact("/stuv", "xy");
		assertThat(filePath2).isEqualTo("/stuv/xy");

		assertThat(FilePathUtil.simplifyPath("../dd/../abc")).isEqualTo("../abc");
		assertThat(FilePathUtil.simplifyPath("../../dd/../abc")).isEqualTo("../../abc");
		assertThat(FilePathUtil.simplifyPath("./abc")).isEqualTo("abc");

	}
}
