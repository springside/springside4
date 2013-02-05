package org.springside.examples.showcase.demos.redis.sessionTimer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;

import com.google.common.collect.Lists;

/**
 * 每秒执行一次Lua Script，从Sorted Set中取出到时间的任务，放入List中等待List中领取.
 * 
 * @author calvin
 */
public class RedisSessionTimerDistributor implements Runnable {

	private static final String HOST = "localhost";
	private static final int PORT = Protocol.DEFAULT_PORT;

	private static String timerKey = "ss.timer";
	private static String jobKey = "ss.job";
	private static int batchSize = 2500;

	private static Jedis jedis;
	private int loop = 1;

	public static void main(String[] args) throws Exception {
		jedis = new Jedis(HOST, PORT);
		jedis.connect();

		RedisSessionTimerDistributor distributor = new RedisSessionTimerDistributor();
		ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1);
		threadPool.scheduleAtFixedRate(distributor, 0, 1, TimeUnit.SECONDS);

		System.out.println("Hit Any Key to stop");
		System.in.read();
		System.out.println("Shuting down");
		threadPool.shutdownNow();
		threadPool.awaitTermination(3, TimeUnit.SECONDS);

		jedis.disconnect();
	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		//TODO: add to ack sorted set , and try to run multi key for lpush/zadd
		String script = "local jobs=redis.call('zrangebyscore',KEYS[1],0,ARGV[1])\n"
				+ " for i=1,ARGV[2] do \n  redis.call('lpush',KEYS[2],jobs[i])\n end\n"
				+ " redis.call('zremrangebyscore',KEYS[1],0,ARGV[1])";
		jedis.eval(script, Lists.newArrayList(timerKey, jobKey),
				Lists.newArrayList(String.valueOf(loop * batchSize), String.valueOf(batchSize)));
		loop++;
		System.out.println("Times spend " + (System.currentTimeMillis() - startTime));
	}
}
