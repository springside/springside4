/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.demos.schedule;

import static org.assertj.core.api.Assertions.*;

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

/**
 * Quartz可集群Timer Job测试.
 * 
 * @author calvin
 */
@Category(UnStable.class)
@DirtiesContext
@ContextConfiguration(locations = { "/applicationContext.xml", "/schedule/applicationContext-quartz-timer-cluster.xml" })
public class QuartzTimerClusterJobTest extends SpringTransactionalTestCase {

	private static LogbackListAppender appender;

	@BeforeClass
	public static void initLogger() {
		// 加载测试用logger appender
		appender = new LogbackListAppender();
		appender.addToLogger(QuartzClusterableJob.class.getName() + ".quartz cluster job");
	}

	@AfterClass
	public static void removeLogger() {
		appender.removeFromLogger(QuartzClusterableJob.class);
	}

	@Test
	public void scheduleJob() throws Exception {
		// 等待任务延时2秒启动并执行完毕
		Threads.sleep(4000);

		// 验证任务已执行
		assertThat(appender.getLogsCount()).isEqualTo(1);
		assertThat(appender.getFirstMessage()).isEqualTo("There are 6 user in database, on node default.");
	}
}
