package org.springside.examples.showcase.demos.redis.job;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;

/**
 * 每秒执行一次Lua Script，从ss.timer(Sorted Set)中取出到时的任务，放入ss.job(List)和ss.ack(Sorted Set)中等待领取和完成呢个确认.
 * 
 * @author calvin
 */
public class JobManager implements Runnable {

	public static final String TIMER_KEY = "ss.job:schedule";
	public static final String JOB_KEY = "ss.job:queue";
	public static final String ACK_KEY = "ss.job:ack";
	public static final int EXPECT_TPS = 2500;
	public static final int DELAY_SECONDS = 10;

	public static final String DEFAULT_HOST = "localhost";
	public static final int DEFAULT_PORT = Protocol.DEFAULT_PORT;
	public static final int DEFAULT_TIMEOUT = 5000;

	private static final int PRINT_BETWEEN_SECONDS = 20;

	private Jedis jedis;
	private String scriptSha;
	private int loop = 1;
	private AtomicLong totalTime = new AtomicLong(0);
	private RateLimiter printRate = RateLimiter.create(1d / PRINT_BETWEEN_SECONDS);

	public static void main(String[] args) throws Exception {

		JobManager distributor = new JobManager();
		distributor.setUp();
		try {
			ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1);
			threadPool.scheduleAtFixedRate(distributor, 10, 1, TimeUnit.SECONDS);

			System.out.println("Hit enter to stop.");
			while (true) {
				char c = (char) System.in.read();
				if (c == '\n') {
					System.out.println("Shuting down");
					threadPool.shutdownNow();
					threadPool.awaitTermination(3, TimeUnit.SECONDS);
				}
			}

		} finally {
			distributor.tearDown();
		}
	}

	public void setUp() {
		jedis = new Jedis(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_TIMEOUT);
		String script = "local jobWithScores=redis.call('zrangebyscore', KEYS[1], '-inf', ARGV[1], 'withscores')\n";
		script += "      redis.call('zremrangebyscore', KEYS[1], '-inf', ARGV[1])\n";
		script += "      for i=1,ARGV[2] do \n";
		script += "          redis.call('lpush', KEYS[2], jobWithScores[i*2-1])\n";
		script += "      end\n";
		script += "      for i=1,ARGV[2] do \n";
		script += "          redis.call('zadd', KEYS[3], jobWithScores[i*2], jobWithScores[i*2-1])\n";
		script += "      end\n";

		System.out.println("Lua Scripts is:\n" + script);
		scriptSha = jedis.scriptLoad(script);
	}

	public void tearDown() {
		jedis.disconnect();
	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();

		jedis.evalsha(scriptSha, Lists.newArrayList(TIMER_KEY, JOB_KEY, ACK_KEY),
				Lists.newArrayList(String.valueOf(startTime), String.valueOf(EXPECT_TPS)));

		long spendTime = System.currentTimeMillis() - startTime;
		totalTime.addAndGet(spendTime);
		loop++;

		if (printRate.tryAcquire()) {
			System.out.printf("Last time %,d ms, average time %,d ms \n", spendTime, totalTime.longValue() / loop);
		}
	}
}
