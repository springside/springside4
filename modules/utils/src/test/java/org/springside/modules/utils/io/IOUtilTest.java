package org.springside.modules.utils.io;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.junit.Test;

public class IOUtilTest {

	@Test
	public void read() throws IOException {
		assertThat(IOUtil.toString(ResourcesUtil.asStream("test.txt"))).isEqualTo("ABCDEFG\nABC");
		assertThat(IOUtil.toLines(ResourcesUtil.asStream("test.txt"))).hasSize(2).containsExactly("ABCDEFG", "ABC");
	}

}
