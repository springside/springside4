/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.test.benchmark;


/**
 * Benchmark中任务线程的基类.
 * 
 * @see ConcurrentBenchmark
 */
public abstract class BenchmarkTask implements Runnable {

	protected int taskSequence;
	protected ConcurrentBenchmark parent;

	@Override
	public void run() {
		setUp();
		onThreadStart();
		try {
			for (int i = 1; i <= parent.loopCount; i++) {
				execute(i);
				parent.incCounter();
			}
		} finally {
			tearDown();
			onThreadFinish();
		}
	}

	abstract protected void execute(final int requestSequence);

	/**
	 * Must be invoked when each thread after the setup().
	 */
	protected void onThreadStart() {
		parent.startLock.countDown();
		// wait for all other threads ready
		try {
			parent.startLock.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Must be invoked when each thread finish loop, before the tearDown().
	 */
	protected void onThreadFinish() {
		// notify test finish
		parent.finishLock.countDown();
	}

	/**
	 * Override for thread local connection and data setup.
	 */
	protected void setUp() {
	}

	/**
	 * Override for thread local connection and data cleanup.
	 */
	protected void tearDown() {
	}
}
