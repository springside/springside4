package org.springside.modules.utils.number;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class NumberUtilTest {

	@Test
	public void toBytes() {
		byte[] bytes = NumberUtil.toBytes(1);
		assertThat(bytes).hasSize(4).containsSequence((byte) 0, (byte) 0, (byte) 0, (byte) 1);

		bytes = NumberUtil.toBytes(257);
		assertThat(bytes).containsSequence((byte) 0, (byte) 0, (byte) 1, (byte) 1);

		byte[] bytes2 = NumberUtil.toBytes(1L);
		assertThat(bytes2).hasSize(8);

		bytes = NumberUtil.toBytes(257L);
		assertThat(bytes).containsSequence((byte) 0, (byte) 0, (byte) 1, (byte) 1);
	}

}
