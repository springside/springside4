package org.springside.modules.nosql.redis.scheduler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springside.modules.nosql.redis.JedisScriptExecutor;
import org.springside.modules.nosql.redis.JedisTemplate;
import org.springside.modules.utils.Threads;
import org.springside.modules.utils.Threads.WrapExceptionRunnable;

import redis.clients.jedis.JedisPool;

import com.google.common.collect.Lists;

/**
 * 定时分发任务。 启动线程定时从sleeping job sorted set 中取出到期的任务放入ready job list.
 * 线程池可自行创建，也可以从外部传入共用。
 * 
 * @author calvin
 */
public class JobDispatcher implements Runnable {
	public static final String DEFAULT_DISPATCH_LUA_FILE = "classpath:/redis/dispatch.lua";

	private static Logger logger = LoggerFactory.getLogger(JobDispatcher.class);

	private static final AtomicInteger poolNumber = new AtomicInteger(1);
	private ScheduledExecutorService internalScheduledThreadPool;
	private ScheduledFuture dispatchJob;

	private JedisTemplate jedisTemplate;

	private JedisScriptExecutor scriptExecutor;
	private String scriptHash;

	private List<String> keys;
	private String readyJobKey;
	private String sleepingJobKey;
	private String dispatchCounterKey;

	public JobDispatcher(String jobName, JedisPool jedisPool) {
		this(jobName, jedisPool, DEFAULT_DISPATCH_LUA_FILE);
	}

	public JobDispatcher(String jobName, JedisPool jedisPool, String scriptPath) {
		sleepingJobKey = Keys.getSleepingJobKey(jobName);
		readyJobKey = Keys.getReadyJobKey(jobName);
		dispatchCounterKey = Keys.getDispatchCounterKey(jobName);
		keys = Lists.newArrayList(sleepingJobKey, readyJobKey, dispatchCounterKey);

		jedisTemplate = new JedisTemplate(jedisPool);
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
	public void start(long periodMillis) {
		internalScheduledThreadPool = Executors.newScheduledThreadPool(1,
				Threads.buildJobFactory("Job-Dispatcher-" + poolNumber.getAndIncrement() + "-%d"));
		start(periodMillis, internalScheduledThreadPool);
	}

	/**
	 * 启动分发线程, 使用传入的scheduler线程池.
	 */
	public void start(long periodMillis, ScheduledExecutorService scheduledThreadPool) {
		dispatchJob = scheduledThreadPool.scheduleAtFixedRate(new WrapExceptionRunnable(this), 0, periodMillis,
				TimeUnit.MILLISECONDS);
	}

	/**
	 * 停止分发任务，如果是自行创建的threadPool则自行销毁。
	 */
	public void stop() {
		dispatchJob.cancel(false);

		if (internalScheduledThreadPool != null) {
			Threads.normalShutdown(internalScheduledThreadPool, 5, TimeUnit.SECONDS);
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

	/**
	 * 获取未达到触发条件进行分发的Job数量.
	 */
	public long getSleepingJobNumber() {
		return jedisTemplate.zcard(sleepingJobKey);
	}

	/**
	 * 获取已分发但未被执行的Job数量.
	 */
	public long getReadyJobNumber() {
		return jedisTemplate.llen(readyJobKey);
	}

	/**
	 * 获取已分发的Job数量。
	 */
	public long getDispatchNumber() {
		String result = jedisTemplate.get(dispatchCounterKey);
		return result != null ? Long.valueOf(result) : 0;
	}

	/**
	 * 重置已分发的Job数量计数器.
	 */
	public void restDispatchNumber() {
		jedisTemplate.set(dispatchCounterKey, "0");
	}
}
