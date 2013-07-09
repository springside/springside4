package org.springside.examples.showcase.demos.schedule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.TaskUtils;
import org.springside.modules.utils.Threads;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * 用JDKScheduledThreadPoolExecutor定时执行的任务。
 * 相比Spring的Task NameSpace配置方, 不需要反射調用，并强化了退出超时控制.
 * 
 * @author calvin
 */
public class JdkTimerJob implements Runnable {

	private int initialDelay = 0;

	private int period = 0;

	private int shutdownTimeout = Integer.MAX_VALUE;

	private ScheduledExecutorService scheduledExecutorService;

	@Autowired
	private UserCountScanner userCountScanner;

	@PostConstruct
	public void start() throws Exception {
		Validate.isTrue(period > 0);

		// 任何异常不会中断schedule执行, 由Spring TaskUtils的LOG_AND_SUPPRESS_ERROR_HANDLER進行处理
		Runnable task = TaskUtils.decorateTaskWithErrorHandler(this, null, true);

		// 创建单线程的SechdulerExecutor,并用guava的ThreadFactoryBuilder设定生成线程的名称
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(
				"JdkTimerJob-%1$d").build());

		// scheduleAtFixedRatefixRate() 固定任务两次启动之间的时间间隔.
		// scheduleAtFixedDelay() 固定任务结束后到下一次启动间的时间间隔.
		scheduledExecutorService.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
	}

	@PreDestroy
	public void stop() {
		Threads.normalShutdown(scheduledExecutorService, shutdownTimeout, TimeUnit.SECONDS);
	}

	/**
	 * 定时打印当前用户数到日志.
	 */
	@Override
	public void run() {
		userCountScanner.executeByJdk();
	}

	/**
	 * 设置任务初始启动延时时间.
	 */
	public void setInitialDelay(int initialDelay) {
		this.initialDelay = initialDelay;
	}

	/**
	 * 设置任务间隔时间,单位秒.
	 */
	public void setPeriod(int period) {
		this.period = period;
	}

	/**
	 * 设置normalShutdown的等待时间, 单位秒.
	 */
	public void setShutdownTimeout(int shutdownTimeout) {
		this.shutdownTimeout = shutdownTimeout;
	}
}
