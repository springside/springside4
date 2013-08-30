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
import org.springside.modules.nosql.redis.JedisTemplate;
import org.springside.modules.nosql.redis.JedisTemplate.JedisAction;
import org.springside.modules.utils.Threads;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

/*
 * 简单的阻塞取出任务。
 */
public class SimpleJobConsumer {

	public static final int DEFAULT_CONNECTION_RETRY_MILLS = 5000;
	public static final int DEFAULT_POPUP_TIMEOUT_SECONDS = 5;

	private static Logger logger = LoggerFactory.getLogger(SimpleJobConsumer.class);

	private JedisTemplate jedisTemplate;
	private String readyJobKey;

	public SimpleJobConsumer(String jobName, JedisPool jedisPool) {
		jedisTemplate = new JedisTemplate(jedisPool);
		readyJobKey = Keys.getReadyJobKey(jobName);
	}

	/**
	 * 阻塞直到返回任务，如果五5秒内无任务到达，返回null.
	 * 如发生redis连接异常, 线程会sleep 5秒后返回null，
	 */
	public String popupJob() {

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
			return nameValuePair.get(1);
		} else {
			return null;
		}
	}
}
