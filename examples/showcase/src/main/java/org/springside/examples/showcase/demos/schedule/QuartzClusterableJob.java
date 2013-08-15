package org.springside.examples.showcase.demos.schedule;

import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springside.examples.showcase.service.AccountService;

/**
 * 被Spring的Quartz JobDetailBean定时执行的Job类, 支持持久化到数据库实现Quartz集群.
 * 
 * 因为需要被持久化, 不能有用XXService等不能被持久化的成员变量,
 * 只能在每次调度时从QuartzJobBean注入的applicationContext中动态取出.
 * 
 * @author calvin
 */
public class QuartzClusterableJob extends QuartzJobBean {

	private static Logger logger = LoggerFactory.getLogger(QuartzClusterableJob.class);

	private ApplicationContext applicationContext;

	/**
	 * 从SchedulerFactoryBean注入的applicationContext.
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * 定时打印当前用户数到日志.
	 */
	@Override
	protected void executeInternal(JobExecutionContext ctx) throws JobExecutionException {
		AccountService accountService = applicationContext.getBean(AccountService.class);
		Map config = (Map) applicationContext.getBean("timerJobConfig");

		long userCount = accountService.getUserCount();
		String nodeName = (String) config.get("nodeName");

		logger.info("There are {} user in database, printed by quartz cluster job on node {}.", userCount, nodeName);
	}
}
