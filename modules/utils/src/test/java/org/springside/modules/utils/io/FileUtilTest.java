package org.springside.modules.utils.io;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class FileUtilTest {

	@Test
	public void read() throws IOException {
		File file = FileUtil.createTempFile("abc", "tmp");
		String content = "haha";
		FileUtil.write(content, file);

		String result = FileUtil.toString(file);
		assertThat(result).isEqualTo(content);

	}

}
