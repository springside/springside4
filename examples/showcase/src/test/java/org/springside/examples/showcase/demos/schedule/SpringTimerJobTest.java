package org.springside.examples.showcase.demos.schedule;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.category.UnStable;
import org.springside.modules.test.log.LogbackListAppender;
import org.springside.modules.test.spring.SpringTransactionalTestCase;
import org.springside.modules.utils.Threads;

@Category(UnStable.class)
@DirtiesContext
@ContextConfiguration(locations = { "/applicationContext.xml", "/schedule/applicationContext-spring-scheduler.xml" })
public class SpringTimerJobTest extends SpringTransactionalTestCase {

	private static LogbackListAppender appender;

	@BeforeClass
	public static void initLogger() {
		// 加载测试用logger appender
		appender = new LogbackListAppender();
		appender.addToLogger(UserCountScanner.class);
	}

	@AfterClass
	public static void removeLogger() {
		appender.removeFromLogger(UserCountScanner.class);
	}

	@Test
	public void scheduleJob() throws Exception {
		// 等待任务执行完毕
		Threads.sleep(2000);

		// 验证任务已执行
		assertEquals(1, appender.getLogsCount());
		assertEquals("There are 6 user in database, printed by spring timer job by xml.", appender.getFirstMessage());
	}
}
