package org.springside.modules.utils.concurrent;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import org.springside.modules.utils.base.Platforms;
import org.springside.modules.utils.concurrent.jsr166e.LongAdder;

public class ConcurrentTools {

	/**
	 * 返回没有激烈CAS冲突的LongAdder, 并发的＋1将在不同的Counter里进行，只在取值时将多个Counter求和.
	 * 
	 * 为了保持版本兼容性，采用移植于Guava的较早的版本
	 */
	public static LongAdder newCounter() {
		return new LongAdder();
	}

	/**
	 * 返回无锁的ThreadLocalRandom
	 * 
	 * 如果JDK6，使用移植于JDK7的版本，否则使用JDK自带
	 */
	public static Random threadLocalRandom() {
		if (Platforms.IS_ATLEASET_JAVA7) {
			return java.util.concurrent.ThreadLocalRandom.current();
		} else {
			return org.springside.modules.utils.concurrent.jsr166e.ThreadLocalRandom.current();
		}
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
	public static CyclicBarrier cyclicBarrier(int count){
		return new CyclicBarrier(count);
	}
}
