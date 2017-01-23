package org.springside.modules.utils.io;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.springside.modules.utils.base.Platforms;

import com.google.common.io.Files;

public class FilePathUtilTest {

	char sep = Platforms.FILE_PATH_SEPARATOR_CHAR;

	@Test
	public void pathName() {
		String filePath = FilePathUtil.contact(sep + "abc", "ef");
		assertThat(filePath).isEqualTo(sep + "abc" + sep + "ef");

		String filePath2 = FilePathUtil.contact(sep + "stuv", "xy");
		assertThat(filePath2).isEqualTo(sep + "stuv" + sep + "xy");

		assertThat(FilePathUtil.simplifyPath("../dd/../abc")).isEqualTo("../abc");
		assertThat(FilePathUtil.simplifyPath("../../dd/../abc")).isEqualTo("../../abc");
		assertThat(FilePathUtil.simplifyPath("./abc")).isEqualTo("abc");
	}

	@Test
	public void getJarPath() {
		System.out.println("the jar file contains Files.class" + FilePathUtil.getJarPath(Files.class));
		assertThat(FilePathUtil.getJarPath(Files.class)).endsWith("guava-20.0.jar");
	}
}
