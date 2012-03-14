package org.springside.examples.showcase.schedule;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springside.examples.showcase.common.service.AccountManager;
import org.springside.modules.utils.Threads;

/**
 * 使用Spring的ThreadPoolTaskScheduler执行Cron式任务的类, 并强化了退出控制.
 */
public class SpringCronJob implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(SpringCronJob.class);

	private String cronExpression;

	private int shutdownTimeout = Integer.MAX_VALUE;

	private ThreadPoolTaskScheduler threadPoolTaskScheduler;

	private AccountManager accountManager;

	@PostConstruct
	public void start() {
		Validate.notBlank(cronExpression);

		threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setThreadNamePrefix("SpringCronJob");
		threadPoolTaskScheduler.initialize();

		threadPoolTaskScheduler.schedule(this, new CronTrigger(cronExpression));
	}

	@PreDestroy
	public void stop() {
		ScheduledExecutorService scheduledExecutorService = threadPoolTaskScheduler.getScheduledExecutor();

		Threads.normalShutdown(scheduledExecutorService, shutdownTimeout, TimeUnit.SECONDS);

	}

	/**
	 * 定时打印当前用户数到日志.
	 */
	@Override
	public void run() {
		long userCount = accountManager.getUserCount();
		logger.info("There are {} user in database, printed by spring cron job.", userCount);
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	/**
	 * 设置gracefulShutdown的等待时间,单位秒.
	 */
	public void setShutdownTimeout(int shutdownTimeout) {
		this.shutdownTimeout = shutdownTimeout;
	}

	@Autowired
	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}
}
