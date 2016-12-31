package org.springside.modules.utils.concurrent;

import java.util.HashMap;
import java.util.Map;

import com.google.common.annotations.Beta;

/**
 * 存储于ThreadLocal的Map, 用于存储上下文.
 */
@Beta
public class ThreadLocalContext {

	private static ThreadLocal<Map<String, Object>> contextMap = new ThreadLocal<Map<String, Object>>() {
		@Override
		protected Map<String, Object> initialValue() {
			return new HashMap<String, Object>();
		}
	};

	/**
	 * 放入ThreadLocal的上下文信息.
	 */
	public static void put(String key, Object value) {
		contextMap.get().put(key, value);
	}

	/**
	 * 取出ThreadLocal的上下文信息.
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
