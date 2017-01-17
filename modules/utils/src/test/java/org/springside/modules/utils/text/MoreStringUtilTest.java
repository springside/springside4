package org.springside.modules.utils.text;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.Test;

public class MoreStringUtilTest {
	@Test
	public void split() {

		List<String> result = MoreStringUtil.split("192.168.0.1", '.', 4);
		assertThat(result).hasSize(4).containsSequence("192", "168", "0", "1");

		result = MoreStringUtil.split("192.168..1", '.', 4);
		assertThat(result).hasSize(3).containsSequence("192", "168", "1");

		result = MoreStringUtil.split("192.168.0.", '.', 4);
		assertThat(result).hasSize(3).containsSequence("192", "168", "0");

		assertThat(MoreStringUtil.split(null, '.', 4)).isNull();

		assertThat(MoreStringUtil.split("", '.', 4)).hasSize(0);

	}

	@Test
	public void other() {
		String str = "abc";
		assertThat(MoreStringUtil.endWith(str, 'c')).isTrue();
		assertThat(MoreStringUtil.endWith(str, 'b')).isFalse();
		assertThat(MoreStringUtil.endWith(null, 'b')).isFalse();
		assertThat(MoreStringUtil.endWith("", 'b')).isFalse();
	}

	@Test
	public void utf8EncodedLength() {
		assertThat(MoreStringUtil.utf8EncodedLength("ab12")).isEqualTo(4);
		assertThat(MoreStringUtil.utf8EncodedLength("中文")).isEqualTo(6);
	}

}
