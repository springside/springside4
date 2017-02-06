package org.springside.modules.utils.concurrent;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.springside.modules.utils.concurrent.jsr166e.LongAdder;

public class ConcurrentToolsTest {

	@Test
	public void longAdder() {
		LongAdder counter = ConcurrentTools.longAdder();
		counter.increment();
		counter.add(2);
		assertThat(counter.longValue()).isEqualTo(3L);
	}

}
