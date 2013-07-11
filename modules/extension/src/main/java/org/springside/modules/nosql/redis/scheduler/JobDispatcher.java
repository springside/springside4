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
import org.springframework.core.io.ResourceLoader;
import org.springside.modules.nosql.redis.JedisScriptExecutor;
import org.springside.modules.utils.Threads.WrapExceptionRunnable;

import redis.clients.jedis.JedisPool;

import com.google.common.collect.Lists;

public class JobDispatcher implements Runnable {
	public static final String DEFAULT_DISPATCH_LUA_FILE = "classpath:/redis/dispatch.lua";

	private static Logger logger = LoggerFactory.getLogger(JobDispatcher.class);

	private ScheduledExecutorService threadPool;

	private JedisScriptExecutor scriptExecutor;

	private String scriptHash;

	private List<String> keys;

	public JobDispatcher(String jobName, JedisPool jedisPool) {
		this(jobName, jedisPool, DEFAULT_DISPATCH_LUA_FILE);
	}

	public JobDispatcher(String jobName, JedisPool jedisPool, String scriptPath) {
		this.scriptExecutor = new JedisScriptExecutor(jedisPool);
		keys = Lists.newArrayList(jobName + ".job:sleeping", jobName + ".job:ready");
		loadLuaScript(scriptPath);
	}

	private void loadLuaScript(String scriptPath) {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource(scriptPath);
		try {
			String script = FileUtils.readFileToString(resource.getFile());
			scriptHash = scriptExecutor.load(script);
		} catch (IOException e) {
			throw new IllegalStateException(DEFAULT_DISPATCH_LUA_FILE + "not exist", e);
		}
	}

	public void start(long periodMilliseconds) {
		threadPool = Executors.newScheduledThreadPool(1);
		threadPool.scheduleAtFixedRate(new WrapExceptionRunnable(this), 0, periodMilliseconds, TimeUnit.MILLISECONDS);
	}

	public void stop() {
		threadPool.shutdownNow();
		try {
			if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
				logger.error("Job dispatcher terminate failed!");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void run() {
		long currTime = System.currentTimeMillis();
		List<String> args = Lists.newArrayList(String.valueOf(currTime));
		scriptExecutor.execute(scriptHash, keys, args);
		long luaExecTime = System.currentTimeMillis() - currTime;
		logger.debug("Execution Time={}ms.", luaExecTime);
	}
}
