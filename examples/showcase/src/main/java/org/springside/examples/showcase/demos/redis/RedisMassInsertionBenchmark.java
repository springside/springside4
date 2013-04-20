package org.springside.examples.showcase.demos.redis;

import org.springside.modules.mapper.JsonMapper;
import org.springside.modules.test.benchmark.BenchmarkTask;
import org.springside.modules.test.benchmark.ConcurrentBenchmark;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Protocol;

/**
 * 测试Redis批量插入时的性能, 使用JSON格式存储数据, 使用PipeLine加速.
 * 
 * 可用系统参数重置相关变量，@see RedisCounterBenchmark
 * 
 * @author calvin
 */
public class RedisMassInsertionBenchmark extends ConcurrentBenchmark {
	private static final int DEFAULT_THREAD_COUNT = 50;
	private static final long DEFAULT_LOOP_COUNT = 100000;
	private static final int PRINT_BETWEEN_SECONDS = 10;

	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = Protocol.DEFAULT_PORT;
	private static final int DEFAULT_TIMEOUT = Protocol.DEFAULT_TIMEOUT;

	private String keyPrefix = "ss.map:";
	private int batchSize = 10;

	private JsonMapper jsonMapper = new JsonMapper();
	private JedisPool pool;

	public static void main(String[] args) throws Exception {
		RedisMassInsertionBenchmark benchmark = new RedisMassInsertionBenchmark(DEFAULT_THREAD_COUNT,
				DEFAULT_LOOP_COUNT, PRINT_BETWEEN_SECONDS);
		benchmark.execute();
	}

	public RedisMassInsertionBenchmark(int defaultThreadCount, long defaultLoopCount, int printBetweenSeconds) {
		super(defaultThreadCount, defaultLoopCount, printBetweenSeconds);
	}

	@Override
	protected void setUp() {
		//create jedis pool
		pool = Utils.createJedisPool(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_TIMEOUT, threadCount);

		//remove all keys
		Jedis jedis = pool.getResource();
		try {
			jedis.flushDB();
		} finally {
			pool.returnResource(jedis);
		}
	}

	@Override
	protected void tearDown() {
		pool.destroy();
	}

	@Override
	protected BenchmarkTask createTask(int taskSequence) {
		return new MassInsertionTask(taskSequence, this);
	}

	public class MassInsertionTask extends BenchmarkTask {

		public MassInsertionTask(int taskSequence, ConcurrentBenchmark parent) {
			super(taskSequence, parent);
		}

		@Override
		public void run() {
			Jedis jedis = pool.getResource();
			onThreadStart();

			try {
				Pipeline pl = jedis.pipelined();

				for (int i = 0; i < loopCount; i++) {
					String key = new StringBuilder().append(keyPrefix).append(taskSequence).append(":").append(i)
							.toString();
					Session session = new Session(key);
					session.setAttrbute("name", key);
					session.setAttrbute("seq", i);
					session.setAttrbute("address", "address:" + i);
					session.setAttrbute("tel", "tel:" + i);

					pl.set(session.getId(), jsonMapper.toJson(session));

					if (i % batchSize == 0) {
						pl.sync();
						printProgressMessage(i);
					}
				}
				pl.sync();
			} finally {
				onThreadFinish();
				pool.returnResource(jedis);
			}
		}
	}
}
