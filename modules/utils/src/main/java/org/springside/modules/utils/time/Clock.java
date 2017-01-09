/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.utils.time;

import java.util.Date;

/**
 * 日期提供者, 使用它而不是直接取得系统时间, 方便测试.
 * 
 * 平时使用Clock.DEFAULT，测试时替换为DummyClock，可准确控制时间变化而不用Thread.sleep()等待时间流逝.
 */
public interface Clock {

	Clock DEFAULT = new DefaultClock();

	/**
	 * 系统当前时间
	 */
	Date currentDate();

	/**
	 * 系统当前时间戳
	 */
	long currentTimeMillis();

	/**
	 * 操作系统启动到现在的纳秒数，与系统时间是完全独立的两个时间体系
	 */
	long nanoTime();

	/**
	 * 默认时间提供者，返回当前的时间，线程安全。
	 */
	class DefaultClock implements Clock {

		@Override
		public Date currentDate() {
			return new Date();
		}

		@Override
		public long currentTimeMillis() {
			return System.currentTimeMillis();
		}

		@Override
		public long nanoTime() {
			return System.nanoTime();
		}
	}

	/**
	 * 可配置的时间提供者，用于测试.
	 */
	class DummyClock implements Clock {

		private long time;
		private long nanoTme;

		public DummyClock() {
		}

		public DummyClock(Date date) {
			this.time = date.getTime();
		}

		public DummyClock(long time) {
			this.time = time;
		}

		@Override
		public Date currentDate() {
			return new Date(time);
		}

		@Override
		public long currentTimeMillis() {
			return time;
		}

		@Override
		public long nanoTime() {
			return nanoTme;
		}

		/**
		 * 重新设置日期。
		 */
		public void update(Date newDate) {
			time = newDate.getTime();
		}

		/**
		 * 重新设置时间。
		 */
		public void update(long newTime) {
			this.time = newTime;
		}

		/**
		 * 滚动时间.
		 */
		public void increaseTime(int millis) {
			time += millis;
		}

		/**
		 * 滚动时间.
		 */
		public void decreaseTime(int millis) {
			time -= millis;
		}

		public void setNanoTime(long nanoTime) {
			this.nanoTme = nanoTime;
		}
	}
}
