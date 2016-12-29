package org.springside.modules.utils.thread;

import java.util.HashMap;
import java.util.Map;

/**
 * 存储于ThreadLocal的Map, 用于存储上下文.
 */
public class ThreadLocalContext {

	private static ThreadLocal<Map<String, Object>> contextMap = new ThreadLocal<Map<String, Object>>() {
		@Override
		protected Map<String, Object> initialValue() {
			return new HashMap<String, Object>();
		}
	};

	/**
	 * 放入ThreadLocal.
	 */
	public static void put(String key, Object value) {
		contextMap.get().put(key, value);
	}

	/**
	 * 取出ThreadLocal.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(String key) {
		return (T) (contextMap.get().get(key));
	}

	/**
	 * 清理ThreadLocal的Context内容.
	 */
	public static void reset() {
		contextMap.get().clear();
	}
}
