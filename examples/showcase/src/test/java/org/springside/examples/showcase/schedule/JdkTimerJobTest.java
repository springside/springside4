package org.springside.examples.showcase.schedule;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springside.modules.test.category.UnStable;
import org.springside.modules.test.log.Log4jMockAppender;
import org.springside.modules.test.spring.SpringTransactionalTestCase;
import org.springside.modules.utils.Threads;

@Category(UnStable.class)
@DirtiesContext
@ContextConfiguration(locations = { "/applicationContext.xml", "/schedule/applicationContext-jdk-timer.xml" })
@TransactionConfiguration(transactionManager = "defaultTransactionManager")
public class JdkTimerJobTest extends SpringTransactionalTestCase {

	@Test
	public void scheduleJob() throws Exception {

		//加载测试用logger appender
		Log4jMockAppender appender = new Log4jMockAppender();
		appender.addToLogger(JdkExecutorJob.class);

		//等待任务启动
		Threads.sleep(2000);

		//验证任务已执行
		assertEquals(1, appender.getLogsCount());
		assertEquals("There are 6 user in database, print by jdk timer job.", appender.getFirstMessage());
	}
}
