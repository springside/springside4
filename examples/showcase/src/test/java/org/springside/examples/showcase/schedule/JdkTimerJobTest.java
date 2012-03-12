package org.springside.examples.showcase.schedule;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springside.modules.log.MockLog4jAppender;
import org.springside.modules.test.data.H2Fixtures;
import org.springside.modules.test.spring.SpringTxTestCase;
import org.springside.modules.utils.Threads;

@Ignore("Unstable on Slow Jenkins")
@DirtiesContext
@ContextConfiguration(locations = { "/applicationContext.xml", "/schedule/applicationContext-jdk-timer.xml" })
@TransactionConfiguration(transactionManager = "defaultTransactionManager")
public class JdkTimerJobTest extends SpringTxTestCase {

	@Test
	public void scheduleJob() throws Exception {
		H2Fixtures.reloadAllTable(dataSource, "/data/sample-data.xml");

		//加载测试用logger appender
		MockLog4jAppender appender = new MockLog4jAppender();
		appender.addToLogger(JdkExecutorJob.class);

		//等待任务启动
		Threads.sleep(3000);

		//验证任务已执行
		assertEquals(1, appender.getAllLogs().size());
		assertEquals("There are 6 user in database, print by jdk timer job.", appender.getFirstMessage());
	}
}
