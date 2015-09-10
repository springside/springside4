/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.demos.redis;

import org.springside.modules.nosql.redis.JedisTemplate;
import org.springside.modules.nosql.redis.pool.JedisPool;
import org.springside.modules.nosql.redis.pool.JedisPoolBuilder;
import org.springside.modules.test.benchmark.BenchmarkTask;
import org.springside.modules.test.benchmark.ConcurrentBenchmark;

/**
 * 测试Redis用于计数器时incr()方法的性能.
 * 
 * 可用-Dthread.count, -Dtotal.count 重置测试规模
 * 可用-Dredis.host,-Dredis.port,-Dredis.timeout 重置连接参数
 * 
 * @author calvin
 */
public class RedisCounterBenchmark extends ConcurrentBenchmark {
	private static final int DEFAULT_THREAD_COUNT = 20;
	private static final long DEFAULT_TOTAL_COUNT = 100000;

	private String counterKey = "ss.counter";
	private JedisPool pool;
	private JedisTemplate jedisTemplate;

	public static void main(String[] args) throws Exception {
		RedisCounterBenchmark benchmark = new RedisCounterBenchmark();
		benchmark.execute();
	}

	public RedisCounterBenchmark() {
		super(DEFAULT_THREAD_COUNT, DEFAULT_TOTAL_COUNT);
	}

	@Override
	protected void setUp() {

		pool = new JedisPoolBuilder().setUrl("direct://localhost:6379?poolSize=" + threadCount).buildPool();
		jedisTemplate = new JedisTemplate(pool);

		// 重置Counter
		jedisTemplate.set(counterKey, "0");
	}

	@Override
	protected void tearDown() {
		pool.destroy();
	}

	@Override
	protected BenchmarkTask createTask() {
		return new CounterTask();
	}

	public class CounterTask extends BenchmarkTask {

		@Override
		protected void execute(int requestSequence) {
			jedisTemplate.incr(counterKey);
		}
	}
}
