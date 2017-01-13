package org.springside.modules.utils.concurrent;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import org.springside.modules.utils.concurrent.jsr166.LongAdder;

public class ConcurrentTools {

	/**
	 * 返回没有激烈CAS冲突的LongAdder, 并发的＋1将在不同的Counter里进行，只在取值时将多个Counter求和.
	 */
	public static LongAdder newCounter() {
		return new LongAdder();
	}

	/**
	 * 返回无锁的ThreadLocalRandom
	 */
	public static Random threadLocalRandom() {
		return org.springside.modules.utils.concurrent.jsr166.ThreadLocalRandom.current();
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
