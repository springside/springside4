package org.springside.modules.nosql.redis.scheduler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springside.modules.nosql.redis.JedisScriptExecutor;
import org.springside.modules.utils.Threads.WrapExceptionRunnable;

import redis.clients.jedis.JedisPool;

import com.google.common.collect.Lists;

/**
 * 定时分发任务。 启动线程定时从sleeping job sorted set 中取出到期的任务放入ready job list.
 * 
 * @author calvin
 */
public class JobDispatcher implements Runnable {
	public static final String DEFAULT_DISPATCH_LUA_FILE = "classpath:/redis/dispatch.lua";

	private static Logger logger = LoggerFactory.getLogger(JobDispatcher.class);

	private ScheduledExecutorService scheduledThreadPool;

	private JedisScriptExecutor scriptExecutor;

	private String scriptHash;

	private List<String> keys;

	public JobDispatcher(String jobName, JedisPool jedisPool) {
		this(jobName, jedisPool, DEFAULT_DISPATCH_LUA_FILE);
	}

	public JobDispatcher(String jobName, JedisPool jedisPool, String scriptPath) {
		keys = Lists.newArrayList(Keys.getSleepingJobKey(jobName), Keys.getReadyJobKey(jobName));
		this.scriptExecutor = new JedisScriptExecutor(jedisPool);
		loadLuaScript(scriptPath);
	}

	private void loadLuaScript(String scriptPath) {
		String script;
		try {
			Resource resource = new DefaultResourceLoader().getResource(scriptPath);
			script = FileUtils.readFileToString(resource.getFile());
		} catch (IOException e) {
			throw new IllegalArgumentException(scriptPath + " is not exist.", e);
		}

		scriptHash = scriptExecutor.load(script);
	}

	/**
	 * 启动分发线程, 自行创建scheduler线程池.
	 */
	public void start(long periodMilliseconds) {
		this.scheduledThreadPool = Executors.newScheduledThreadPool(1);
		scheduledThreadPool.scheduleAtFixedRate(new WrapExceptionRunnable(this), 0, periodMilliseconds,
				TimeUnit.MILLISECONDS);
	}

	/**
	 * 停止分发任务, 默认最多延时10秒等候线程关闭.
	 */
	public void stop() {
		scheduledThreadPool.shutdownNow();
		try {
			if (!scheduledThreadPool.awaitTermination(10, TimeUnit.SECONDS)) {
				logger.error("Job dispatcher terminate failed!");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * 以当前时间为参数执行Lua Script分发任务。
	 */
	@Override
	public void run() {
		long currTime = System.currentTimeMillis();
		List<String> args = Lists.newArrayList(String.valueOf(currTime));
		scriptExecutor.execute(scriptHash, keys, args);
	}
}
