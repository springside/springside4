package org.springside.modules.utils.net;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class NetUtilTest {

	@Test
	public void localhost() {
		assertThat(NetUtil.getLocalHost()).isNotEqualTo("127.0.0.1");
	}

}
