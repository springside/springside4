package org.springside.modules.utils.collection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 关于Map的工具集合，
 * 
 * 对于Guava与Java Collections中的常用函数，直接引用.
 * 
 * 对于Apache Commons Collection中的函数，直接移植避免引入依赖.
 * 
 * @author calvin
 */
@SuppressWarnings("unchecked")

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
	public static boolean isNotEmpty(Map<?, ?> map) {
		return (map != null) && !map.isEmpty();
	}

	///////////////// from Guava的构造函数///////////////////

	/**
	 * 根据等号左边的类型, 构造类型正确的HashMap.
	 * 
	 * @see com.google.common.collect.Maps#newHashMap()
	 */
	public static <K, V> HashMap<K, V> newHashMap() {
		return new HashMap<K, V>();
	}

	/**
	 * 根据等号左边的类型, 构造类型正确的HashMap.
	 * 
	 * 注意HashMap中有0.75的加载因子的影响, 需要进行运算后才能正确初始化HashMap的大小.
	 * 
	 * @see com.google.common.collect.Maps#newHashMap(int)
	 */
	public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int expectedSize) {
		return com.google.common.collect.Maps.newHashMapWithExpectedSize(expectedSize);
	}

	/**
	 * 根据等号左边的类型，构造类型正确的TreeMap.
	 * 
	 * @see com.google.common.collect.Maps#newTreeMap()
	 */
	@SuppressWarnings("rawtypes")
	public static <K extends Comparable, V> TreeMap<K, V> newTreeMap() {
		return new TreeMap<K, V>();
	}

	///////////////// from JDK Collections的常用函数 ///////////////////

	/**
	 * 返回一个空的结构特殊的Map，节约空间. 注意Map不可写.
	 * 
	 * @see java.util.Collections#emptyMap()
	 */
	public static final <K, V> Map<K, V> emptyMap() {
		return (Map<K, V>) Collections.EMPTY_MAP;
	}

	/**
	 * 返回一个只含一个元素但结构特殊的Map，节约空间. 注意Map不可写.
	 * 
	 * @see java.util.Collections#singletonMap(Object, Object)
	 */
	public static <K, V> Map<K, V> singletonMap(K key, V value) {
		return Collections.singletonMap(key, value);
	}

	/**
	 * 如果map为null，转化为一个安全的空Map，注意Map不可写.
	 * 
	 * @see java.util.Collections#emptyMap()
	 */
	public static <K, V> Map<K, V> emptyMapIfNull(final Map<K, V> map) {
		return map == null ? (Map<K, V>) Collections.EMPTY_MAP : map;
	}

}
