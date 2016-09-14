package org.springside.modules.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 存储于ThreadLocal的上下文信息.
 */
public class ThreadLocalContext {

	private static ThreadLocal<Map<String, Object>> context = new ThreadLocal<Map<String, Object>>() {
		@Override
		protected Map<String, Object> initialValue() {
			return new HashMap<String, Object>();
		}
	};

	/**
	 * 放入ThreadLocal的Context.
	 */
	public static void put(String key, Object value) {
		context.get().put(key, value);
	}

	/**
	 * 取出ThreadLocal的Context.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(String key) {
		return (T) (context.get().get(key));
	}

	/**
	 * 清理ThreadLocal的Context内容.
	 */
	public static void reset() {
		context.get().clear();
	}
}
