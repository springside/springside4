package org.springside.modules.utils.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import org.springside.modules.utils.concurrent.jsr166e.LongAdder;
import org.springside.modules.utils.concurrent.throttle.Sampler;

import com.google.common.util.concurrent.RateLimiter;

public class ConcurrentTools {

	/**
	 * 返回没有激烈CAS冲突的LongAdder, 并发的＋1将在不同的Counter里进行，只在取值时将多个Counter求和.
	 * 
	 * 为了保持JDK版本兼容性，统一采用移植版
	 */
	public static LongAdder longAdder() {
		return new LongAdder();
	}

	/**
	 * 返回CountDownLatch
	 */
	public static CountDownLatch countDownLatch(int count) {
		return new CountDownLatch(count);
	}

	/**
	 * 返回CyclicBarrier
	 */
	public static CyclicBarrier cyclicBarrier(int count) {
		return new CyclicBarrier(count);
	}

	/////////// 限流采样 //////
	/**
	 * 返回漏桶算法的RateLimiter
	 * 
	 * @permitsPerSecond 期望的QPS, RateLimiter将QPS平滑到毫秒级别上，但有蓄水及桶外预借的能力.
	 */
	public static RateLimiter rateLimiter(int permitsPerSecond) {
		return RateLimiter.create(permitsPerSecond);
	}

	/**
	 * 返回采样器.
	 * 
	 * @param selectPercent 采样率，在0-100 之间，可以有小数位
	 */
	public static Sampler sampler(double selectPercent) {
		return Sampler.create(selectPercent);
	}
}
