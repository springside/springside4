package org.springside.modules.metrics.utils;

import java.util.Date;

/**
 * 日期提供者，使用它而不是直接取得系统时间，方便测试。
 * 
 * @author calvin
 */
public interface Clock {

	Date getCurrentDate();

	long getCurrentTime();

	static final Clock DEFAULT = new DefaultDateProvider();

	/**
	 * 默认时间提供者，返回当前的时间，线程安全。
	 */
	public static class DefaultDateProvider implements Clock {

		@Override
		public Date getCurrentDate() {
			return new Date();
		}

		@Override
		public long getCurrentTime() {
			return System.currentTimeMillis();
		}

	}

	/**
	 * 可配置的时间提供者，用于测试.
	 */
	public static class MockedDateProvider implements Clock {

		private long time;

		public MockedDateProvider(Date date) {
			this.time = date.getTime();
		}

		public MockedDateProvider(long time) {
			this.time = time;
		}

		@Override
		public Date getCurrentDate() {
			return new Date(time);
		}

		@Override
		public long getCurrentTime() {
			return time;
		}

		/**
		 * 重新设置时间。
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
		 * 增加时间戳.
		 */
		public void incrementTime(int millis) {
			time += millis;
		}
	}
}
