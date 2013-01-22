package org.springside.examples.showcase.demos.redis;

import java.util.Date;

import org.springside.modules.mapper.JsonMapper;
import org.springside.modules.test.benchmark.BenchmarkBase;
import org.springside.modules.test.benchmark.BenchmarkTask;

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
public class RedisMassInsertion extends BenchmarkBase {
	private static final int THREAD_COUNT = 20;
	private static final long LOOP_COUNT = 500000;
	private static final int PRINT_INTERVAL_SECONDS = 10;
	private static final int BATCH_SIZE = 10;

	private static final String HOST = "localhost";
	private static final int PORT = Protocol.DEFAULT_PORT;
	private static final int TIMEOUT = 40000;

	private String keyPrefix = "springside.key_";
	private JsonMapper jsonMapper = new JsonMapper();
	private JedisPool pool;

	public static void main(String[] args) throws Exception {
		RedisMassInsertion benchmark = new RedisMassInsertion(THREAD_COUNT, LOOP_COUNT);
		benchmark.run();
	}

	public RedisMassInsertion(int threadCount, long loopCount) {
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
	protected Runnable getTask(int index) {
		return new MassInsertionTask(index, this, PRINT_INTERVAL_SECONDS);
	}

	public class MassInsertionTask extends BenchmarkTask {

		public MassInsertionTask(int index, BenchmarkBase parent, int printInfoInterval) {
			super(index, parent, printInfoInterval);
		}

		@Override
		public void run() {
			Jedis jedis = pool.getResource();
			Date startTime = onThreadStart();

			try {
				Pipeline pl = jedis.pipelined();
				// start test loop
				for (int i = 0; i < loopCount; i++) {
					String key = new StringBuilder().append(keyPrefix).append(threadIndex).append("_").append(i)
							.toString();
					//set session expired after 100 seconds
					Session session = new Session(key);
					session.addAttrbute("name", key);
					session.addAttrbute("age", i);
					session.addAttrbute("address", "address:" + i);
					session.addAttrbute("tel", "tel:" + i);

					pl.set(session.getId(), jsonMapper.toJson(session));

					if (i % BATCH_SIZE == 0) {
						pl.sync();
						printInfo(startTime, i);
					}
				}
			} finally {
				onThreadFinish();
				pool.returnResource(jedis);
			}
		}

	}
}
