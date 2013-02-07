package org.springside.examples.showcase.demos.redis.sessionTimer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;

/**
 * 每秒执行一次Lua Script，从ss.timer(Sorted Set)中取出到时的任务，放入ss.job(List)和ss.ack(Sorted Set)中等待领取和完成呢个确认.
 * 
 * @author calvin
 */
public class RedisSessionTimerDistributor implements Runnable {

	public static final String TIMER_KEY = "ss.timer";
	public static final String JOB_KEY = "ss.job";
	public static final String ACK_KEY = "ss.ack";

	public static final String HOST = "localhost";
	public static final int PORT = Protocol.DEFAULT_PORT;
	public static final int TIMEOUT = 5000;

	private static final int PRINT_BETWEEN_SECONDS = 20;
	private static int BATCH_SIZE = 2500;

	private Jedis jedis;
	private String scriptSha;
	private int loop = 1;
	private long totalTime;
	private RateLimiter printRate = RateLimiter.create(1d / PRINT_BETWEEN_SECONDS);

	public static void main(String[] args) throws Exception {

		RedisSessionTimerDistributor distributor = new RedisSessionTimerDistributor();
		distributor.setUp();
		try {
			ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1);
			threadPool.scheduleAtFixedRate(distributor, 5, 1, TimeUnit.SECONDS);

			System.out.println("Hit enter to stop.");
			System.in.read();
			System.out.println("Shuting down");
			threadPool.shutdownNow();
			threadPool.awaitTermination(3, TimeUnit.SECONDS);
		} finally {
			distributor.tearDown();
		}
	}

	public void setUp() {
		jedis = new Jedis(HOST, PORT, TIMEOUT);
		String script = "local jobWithScores=redis.call('zrangebyscore', KEYS[1], 0, ARGV[1], 'withscores')\n";
		script += "      for i=1,ARGV[2] do \n";
		script += "          redis.call('lpush', KEYS[2], jobWithScores[i*2-1])\n";
		script += "      end\n";
		script += "      for i=1,ARGV[2] do \n";
		script += "          redis.call('zadd', KEYS[3], jobWithScores[i*2], jobWithScores[i*2-1])\n";
		script += "      end\n";
		script += "      redis.call('zremrangebyscore', KEYS[1], 0, ARGV[1])";

		System.out.println(script);
		scriptSha = jedis.scriptLoad(script);
	}

	public void tearDown() {
		jedis.disconnect();
	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		jedis.evalsha(scriptSha, Lists.newArrayList(TIMER_KEY, JOB_KEY, ACK_KEY),
				Lists.newArrayList(String.valueOf(loop * BATCH_SIZE - 1), String.valueOf(BATCH_SIZE)));
		loop++;
		long spendTime = System.currentTimeMillis() - startTime;
		totalTime += spendTime;
		if (printRate.tryAcquire()) {
			System.out.printf("Average time %d ms \n", totalTime / loop);
		}
	}
}
