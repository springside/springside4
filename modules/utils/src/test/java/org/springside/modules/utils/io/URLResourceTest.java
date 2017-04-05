package org.springside.modules.utils.io;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.springside.modules.utils.base.Platforms;

public class URLResourceTest {

	@Test
	public void resource() throws IOException {
		File file = URLResourceUtil.asFile("classpath://application.properties");
		InputStream is = URLResourceUtil.asStream("classpath://application.properties");
		if (Platforms.IS_WINDOWS) {
			assertThat(FileUtil.toString(file)).isEqualTo("springside.min=1\r\nspringside.max=10");
			assertThat(IOUtil.toString(is)).isEqualTo("springside.min=1\r\nspringside.max=10");
		} else {
			assertThat(FileUtil.toString(file)).isEqualTo("springside.min=1\nspringside.max=10");
			assertThat(IOUtil.toString(is)).isEqualTo("springside.min=1\nspringside.max=10");
		}
		IOUtil.closeQuietly(is);

		try {
			URLResourceUtil.asFile("classpath://notexist.properties");
			fail("should fail");
		} catch (Throwable t) {
			assertThat(t).isInstanceOf(IllegalArgumentException.class);
		}

		try {
			URLResourceUtil.asStream("classpath://notexist.properties");
			fail("should fail");
		} catch (Throwable t) {
			assertThat(t).isInstanceOf(IllegalArgumentException.class);
		}

	}

	@Test
	public void file() throws IOException {
		File file = FileUtil.createTempFile();
		FileUtil.write("haha", file);
		try {
			File file2 = URLResourceUtil.asFile("file://" + file.getAbsolutePath());
			assertThat(FileUtil.toString(file2)).isEqualTo("haha");

			File file2NotExist = URLResourceUtil.asFile("file://" + file.getAbsolutePath() + ".noexist");

			File file3 = URLResourceUtil.asFile(file.getAbsolutePath());
			assertThat(FileUtil.toString(file3)).isEqualTo("haha");
			File file3NotExist = URLResourceUtil.asFile(file.getAbsolutePath() + ".noexist");

		} finally {
			FileUtil.deleteFile(file);
		}

	}

}
