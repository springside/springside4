package org.springside.modules.utils.io;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.springside.modules.utils.base.Platforms;

public class FilePathUtilTest {

  private String sep = Platforms.FILE_PATH_SEPARATOR;

	@Test
	public void getName() {
		assertThat(FilePathUtil.getFileName(sep + "a" + sep + "d" + sep + "b" + sep + "abc.txt")).isEqualTo("abc.txt");
		assertThat(FilePathUtil.getFileName("abc.txt")).isEqualTo("abc.txt");

		assertThat(FilePathUtil.getFileExtension(sep + "a" + sep + "d" + sep + "b" + sep + "abc.txt")).isEqualTo("txt");
		assertThat(FilePathUtil.getFileExtension(sep + "a" + sep + "d" + sep + "b" + sep + "abc")).isEqualTo("");
		assertThat(FilePathUtil.getFileExtension(sep + "a" + sep + "d" + sep + "b" + sep + "abc.")).isEqualTo("");

	}

	@Test
	public void pathName() {
		String filePath = FilePathUtil.contact(sep + "abc" + sep, "ef");
		assertThat(filePath).isEqualTo(sep + "abc" + sep + "ef");

		String filePath2 = FilePathUtil.contact(sep + "stuv", "xy");
		assertThat(filePath2).isEqualTo(sep + "stuv" + sep + "xy");

		assertThat(FilePathUtil.simplifyPath("../dd/../abc")).isEqualTo("../abc");
		assertThat(FilePathUtil.simplifyPath("../../dd/../abc")).isEqualTo("../../abc");
		assertThat(FilePathUtil.simplifyPath("./abc")).isEqualTo("abc");

	}
}
