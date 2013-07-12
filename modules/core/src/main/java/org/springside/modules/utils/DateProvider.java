package org.springside.modules.utils;

import java.util.Date;

/**
 * 日期提供者，使用它而不是直接取得系统时间，方便测试。
 * 
 * @author calvin
 */
public interface DateProvider {

	Date getDate();

	static final DateProvider DEFAULT = new CurrentDateProvider();

	/**
	 * 返回当前的时间。
	 */
	public static class CurrentDateProvider implements DateProvider {

		@Override
		public Date getDate() {
			return new Date();
		}
	}

	/**
	 * 返回设定的时间.
	 */
	public static class ConfigurableDateProvider implements DateProvider {

		private final Date date;

		public ConfigurableDateProvider(Date date) {
			this.date = date;
		}

		@Override
		public Date getDate() {
			return date;
		}
	}

}
