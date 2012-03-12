package org.springside.examples.showcase.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springside.examples.showcase.common.service.AccountManager;

/**
 * 被Spring的Quartz MethodInvokingJobDetailFactoryBean定时执行的普通Spring Bean.
 */
public class QuartzJob {

	private static Logger logger = LoggerFactory.getLogger(QuartzJob.class);

	@Autowired
	private AccountManager accountManager;

	/**
	 * 定时打印当前用户数到日志.
	 */
	public void execute() {
		long userCount = accountManager.getUserCount();
		logger.info("There are {} user in database, printed by quartz local job.", userCount);
	}
}
