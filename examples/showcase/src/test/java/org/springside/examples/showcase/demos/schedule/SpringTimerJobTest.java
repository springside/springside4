package org.springside.examples.showcase.demos.schedule;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.category.UnStable;
import org.springside.modules.test.log.Log4jMockAppender;
import org.springside.modules.test.spring.SpringTransactionalTestCase;
import org.springside.modules.utils.Threads;

@Category(UnStable.class)
@DirtiesContext
@ContextConfiguration(locations = { "/applicationContext.xml", "/schedule/applicationContext-spring-scheduler.xml" })
public class SpringTimerJobTest extends SpringTransactionalTestCase {

	@Test
	public void scheduleJob() throws Exception {

		//加载测试用logger appender
		Log4jMockAppender appender = new Log4jMockAppender();
		appender.addToLogger(UserCountScanner.class);

		//等待任务启动
		Threads.sleep(2000);

		//验证任务已执行
		assertEquals(1, appender.getLogsCount());
		assertEquals("There are 6 user in database, printed by spring timer job by xml.", appender.getFirstMessage());
		appender.removeFromLogger(UserCountScanner.class);
	}
}
