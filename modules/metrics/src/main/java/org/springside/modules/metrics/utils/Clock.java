package org.springside.modules.metrics.utils;

/**
 * 日期提供者，使用它而不是直接取得系统时间，方便测试。
 * 
 * @author calvin
 */
public interface Clock {

	long getCurrentTime();

	static final Clock DEFAULT = new DefaultClock();

	/**
	 * 默认时间提供者，返回当前的时间，线程安全。
	 */
	public static class DefaultClock implements Clock {

		@Override
		public long getCurrentTime() {
			return System.currentTimeMillis();
		}

	}

	/**
	 * 可配置的时间提供者，用于测试.
	 */
	public static class MockedClock implements Clock {

		private long time;

		public MockedClock() {
			this(0);
		}

		public MockedClock(long time) {
			this.time = time;
		}

		@Override
		public long getCurrentTime() {
			return time;
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
