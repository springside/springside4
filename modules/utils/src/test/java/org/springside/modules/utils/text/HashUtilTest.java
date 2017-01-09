package org.springside.modules.utils.text;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.springside.modules.utils.collection.ListUtil;

public class HashUtilTest {
	@Test
	public void hashCodeTest() {
		assertThat(HashUtil.hashCode("a", "b") - HashUtil.hashCode("a", "a")).isEqualTo(1);
		assertThat(HashUtil.hashCode("a", "b")).isEqualTo(HashUtil.hashCode(ListUtil.newArrayList("a", "b")));
	}
}
