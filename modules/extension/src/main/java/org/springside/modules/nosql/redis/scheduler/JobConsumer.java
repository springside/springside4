/*------------------------------------------------------------------------------
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
package org.springside.modules.nosql.redis.scheduler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.nosql.redis.JedisScriptExecutor;
import org.springside.modules.nosql.redis.JedisTemplate;
import org.springside.modules.nosql.redis.JedisTemplate.JedisAction;
import org.springside.modules.utils.Threads;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.google.common.collect.Lists;

/**
 * 持续接收任务并交予JobHandler处理的线程.
 * 分三种方式循环接收任务并交给JobHandler进行处理。
 */
public class JobConsumer implements Runnable {
	public static final int DEFAULT_CONNECTION_RETRY_MILLS = 5000; // for all
	public static final int DEFAULT_POPUP_TIMEOUT_SECONDS = 5; // for simple popup
	public static final int DEFAULT_QUEUE_EMPTY_WAIT_MILLS = 100; // for other 2 popup
	public static final String DEFAULT_RELIABLE_POP_LUA_FILE = "classpath:/redis/reliablepop.lua";
	public static final String DEFAULT_BATCH_POP_LUA_FILE = "classpath:/redis/batchpop.lua";
	public static final boolean DEFAULT_RELIABLE = false;
	public static final int DEFAULT_BATCH_SIZE = 1;

	private static Logger logger = LoggerFactory.getLogger(JobConsumer.class);

	private boolean reliable = DEFAULT_RELIABLE;
	private int batchSize = DEFAULT_BATCH_SIZE;
	private int queueEmptyWaitMills = DEFAULT_QUEUE_EMPTY_WAIT_MILLS;

	private JedisTemplate jedisTemplate;
	private JedisScriptExecutor reliabelPopScriptExecutor;
	private JedisScriptExecutor batchPopScriptExecutor;
	private String reliablePopScriptPath = DEFAULT_RELIABLE_POP_LUA_FILE;
	private String batchPopScriptPath = DEFAULT_BATCH_POP_LUA_FILE;

	private String readyJobKey;
	private String lockJobKey;

	private final JobHandler jobHandler;

	public JobConsumer(String jobName, JedisPool jedisPool, JobHandler jobHandler) {
		jedisTemplate = new JedisTemplate(jedisPool);
		reliabelPopScriptExecutor = new JedisScriptExecutor(jedisPool);
		batchPopScriptExecutor = new JedisScriptExecutor(jedisPool);

		readyJobKey = Keys.getReadyJobKey(jobName);
		lockJobKey = Keys.getLockJobKey(jobName);

		this.jobHandler = jobHandler;
	}

	/**
	 * 启动任务。
	 */
	@Override
	public void run() {
		if ((reliable == false) && (batchSize == 1)) {
			consumeSimpleJob();
		} else if ((reliable == true) && (batchSize == 1)) {
			consumeReliableJob();
		} else {
			consumeBatchJob();
		}
	}

	/**
	 * 循环blocking popup ready job，默认5秒超时。
	 */
	private void consumeSimpleJob() {
		while (!Thread.currentThread().isInterrupted()) {
			List<String> nameValuePair = null;
			try {
				nameValuePair = jedisTemplate.execute(new JedisAction<List<String>>() {
					@Override
					public List<String> action(Jedis jedis) {
						return jedis.brpop(DEFAULT_POPUP_TIMEOUT_SECONDS, readyJobKey);
					}
				});
			} catch (JedisConnectionException e) {
				Threads.sleep(DEFAULT_CONNECTION_RETRY_MILLS);
			}

			if ((nameValuePair != null) && !nameValuePair.isEmpty()) {
				String job = nameValuePair.get(1);
				try {
					jobHandler.handleJob(job);
				} catch (Exception e) {
					// 记录jobHandler流出的异常，然后毫不停顿的继续运行，做个坚强的Listener。
					logger.error("Handler exception for job " + job, e);
				}
			}
		}
	}

	/**
	 * 循环高可靠性的接收单个Job, 取走的job会放入job:lock sorted set中，处理完毕后再清除。
	 */
	private void consumeReliableJob() {
		reliabelPopScriptExecutor.loadFromFile(reliablePopScriptPath);
		List<String> keys = Lists.newArrayList(readyJobKey, lockJobKey);

		while (!Thread.currentThread().isInterrupted()) {
			String job = null;
			try {
				long currTime = System.currentTimeMillis();
				List<String> args = Lists.newArrayList(String.valueOf(currTime));
				job = (String) reliabelPopScriptExecutor.execute(keys, args);
			} catch (JedisConnectionException e) {
				Threads.sleep(DEFAULT_CONNECTION_RETRY_MILLS);
			}

			if (job != null) {
				try {
					jobHandler.handleJob(job);
				} catch (Exception e) {
					// 记录jobHandler流出的异常，然后毫不停顿的继续运行，做个坚强的Listener。
					logger.error("Handler exception for job " + job, e);
				} finally {
					// 暂不支持retry逻辑，已交予JobHandler处理完毕的任务，无论成功与否，删除lockJob.
					jedisTemplate.zrem(lockJobKey, job);
				}
			} else {
				Threads.sleep(queueEmptyWaitMills);
			}
		}
	}

	/**
	 * 循环批量接收Job, 是否将取走的job会放入job:lock中看 reliable 参数。
	 */
	private void consumeBatchJob() {
		batchPopScriptExecutor.loadFromFile(batchPopScriptPath);

		List<String> keys = Lists.newArrayList(readyJobKey, lockJobKey);

		while (!Thread.currentThread().isInterrupted()) {
			List<String> jobs = null;
			try {
				long currTime = System.currentTimeMillis();
				List<String> args = Lists.newArrayList(String.valueOf(currTime), String.valueOf(batchSize),
						String.valueOf(reliable));
				jobs = (List<String>) batchPopScriptExecutor.execute(keys, args);
			} catch (JedisConnectionException e) {
				Threads.sleep(DEFAULT_CONNECTION_RETRY_MILLS);
			}

			if ((jobs != null) && !jobs.isEmpty()) {
				for (String job : jobs) {
					try {
						jobHandler.handleJob(job);
					} catch (Exception e) {
						// 记录jobHandler流出的异常，然后毫不停顿的继续运行，做个坚强的Listener。
						logger.error("Handler exception for job " + job, e);
					} finally {
						// 暂不支持retry逻辑，已交予JobHandler处理完毕的任务，删除lockJob.
						jedisTemplate.zrem(lockJobKey, job);
					}
				}
			} else {
				Threads.sleep(queueEmptyWaitMills);
			}
		}
	}

	public void setReliable(boolean reliable) {
		this.reliable = reliable;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public void setQueueEmptyWaitMills(int queueEmptyWaitMills) {
		this.queueEmptyWaitMills = queueEmptyWaitMills;
	}

	/**
	 * 回调Handler接口定义.
	 */
	public interface JobHandler {
		void handleJob(String job);
	}
}
