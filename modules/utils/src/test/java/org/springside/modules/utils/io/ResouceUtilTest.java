package org.springside.modules.utils.io;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.junit.Test;

public class ResouceUtilTest {

	@Test
	public void test() throws IOException {
		assertThat(ResourceUtil.toString("test.txt")).contains("ABCDEFG");
		assertThat(ResourceUtil.toString(ResouceUtilTest.class, "/test.txt")).contains("ABCDEFG");
		assertThat(ResourceUtil.toLines("test.txt")).containsExactly("ABCDEFG", "ABC");
		assertThat(ResourceUtil.toLines(ResouceUtilTest.class, "/test.txt")).containsExactly("ABCDEFG", "ABC");
	}

}
