package org.springside.modules.utils.concurrent;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.springside.modules.test.log.LogbackListAppender;

public class ThreadDumpperTest {

	@Test
	public void test() {
		ThreadDumpper dumpper = new ThreadDumpper();
		dumpper.threadDumpIfNeed();

		LogbackListAppender appender = new LogbackListAppender();
		appender.addToLogger(ThreadDumpper.class);

		// disable,不输出
		dumpper.setEnable(false);
		dumpper.threadDumpIfNeed();
		assertThat(appender.getAllLogs()).hasSize(0);

		// 设置最少间隔,不输出
		dumpper.setEnable(true);
		dumpper.setLeastInterval(1800);
		dumpper.threadDumpIfNeed();
		assertThat(appender.getAllLogs()).hasSize(0);

	}

}
