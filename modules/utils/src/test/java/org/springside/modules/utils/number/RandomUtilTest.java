package org.springside.modules.utils.number;

import org.junit.Test;

public class RandomUtilTest {

	@Test
	public void getRandom() {
		System.out.println(RandomUtil.secureRandom().nextInt());
		System.out.println(RandomUtil.threadLocalRandom().nextInt());
	}

}
