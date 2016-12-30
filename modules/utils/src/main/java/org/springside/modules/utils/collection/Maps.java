package org.springside.modules.utils.collection;

import java.util.Map;

public class Maps {

	/**
	 * 判断是否为空.
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Map map) {
		return (map == null) || map.isEmpty();
	}

	/**
	 * 判断是否为空.
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isNotEmpty(Map map) {
		return (map != null) && !map.isEmpty();
	}

}
