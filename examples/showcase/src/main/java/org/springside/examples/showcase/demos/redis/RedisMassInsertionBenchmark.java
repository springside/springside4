package org.springside.examples.showcase.demos.redis;

import org.springside.modules.mapper.JsonMapper;
import org.springside.modules.test.benchmark.BenchmarkTask;
import org.springside.modules.test.benchmark.ConcurrentBenchmark;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Protocol;

/**
 * 测试Redis批量插入时的性能, 使用PipeLine加速.
 * 
 * @author calvin
 */
public class RedisMassInsertionBenchmark extends ConcurrentBenchmark {
	private static final int THREAD_COUNT = 20;
	private static final long LOOP_COUNT = 500000;
	private static final int PRINT_BETWEEN_SECONDS = 10;
	private static final int BATCH_SIZE = 10;

	private static final String HOST = "localhost";
	private static final int PORT = Protocol.DEFAULT_PORT;
	private static final int TIMEOUT = 40000;

	private String keyPrefix = "springside.key_";
	private JsonMapper jsonMapper = new JsonMapper();
	private JedisPool pool;

	public static void main(String[] args) throws Exception {
		RedisMassInsertionBenchmark benchmark = new RedisMassInsertionBenchmark(THREAD_COUNT, LOOP_COUNT);
		benchmark.run();
	}

	public RedisMassInsertionBenchmark(int threadCount, long loopCount) {
		super(threadCount, loopCount);
	}

	@Override
	protected void onStart() {
		//create jedis pool
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxActive(THREAD_COUNT);
		pool = new JedisPool(poolConfig, HOST, PORT, TIMEOUT);

		//remove all keys
		Jedis jedis = pool.getResource();
		try {
			jedis.flushDB();
		} finally {
			pool.returnResource(jedis);
		}
	}

	@Override
	protected void onFinish() {
		pool.destroy();
	}

	@Override
	protected BenchmarkTask createTask(int index) {
		return new MassInsertionTask(index, this, PRINT_BETWEEN_SECONDS);
	}

	public class MassInsertionTask extends BenchmarkTask {

		public MassInsertionTask(int index, ConcurrentBenchmark parent, int printBetweenSeconds) {
			super(index, parent, printBetweenSeconds);
		}

		@Override
		public void run() {
			Jedis jedis = pool.getResource();
			onThreadStart();

			try {
				Pipeline pl = jedis.pipelined();

				for (int i = 0; i < loopCount; i++) {
					String key = new StringBuilder().append(keyPrefix).append(threadIndex).append("_").append(i)
							.toString();
					Session session = new Session(key);
					session.setAttrbute("name", key);
					session.setAttrbute("seq", i);
					session.setAttrbute("address", "address:" + i);
					session.setAttrbute("tel", "tel:" + i);

					pl.set(session.getId(), jsonMapper.toJson(session));

					if (i % BATCH_SIZE == 0) {
						pl.sync();
						printProgressMessage(i);
					}
				}
			} finally {
				onThreadFinish();
				pool.returnResource(jedis);
			}
		}
	}
}
