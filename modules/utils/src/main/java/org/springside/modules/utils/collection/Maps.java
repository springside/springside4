package org.springside.modules.utils.collection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 关于Map的工具集合，
 * 
 * 1. 常用函数(如是否为空)
 * 
 * 2. 对于并发Map，增加putIfAbsent(返回最终值版), createIfAbsent这两个重要函数(from Common Lang)
 * 
 * 3. 便捷的构造函数(via guava，并增加了用数组，List等方式初始化Map的函数)
 * 
 * 4. emptyMap,singletonMap,unmodifiedMap (via JDK Collection)
 * 
 * @author calvin
 */
@SuppressWarnings("unchecked")
public class Maps {

	/**
	 * 判断是否为空.
	 */
	public static boolean isEmpty(final Map<?, ?> map) {
		return (map == null) || map.isEmpty();
	}

	/**
	 * 判断是否为空.
	 */
	public static boolean isNotEmpty(final Map<?, ?> map) {
		return (map != null) && !map.isEmpty();
	}

	/**
	 * ConcurrentMap的putIfAbsent()返回之前的Value，此函数封装返回最终存储在Map中的Value
	 * 
	 * @see org.apache.commons.lang3.concurrent.ConcurrentUtils#putIfAbsent(ConcurrentMap, Object, Object)
	 */
	public static <K, V> V putIfAbsentWithFinalValue(final ConcurrentMap<K, V> map, final K key, final V value) {
		final V result = map.putIfAbsent(key, value);
		return result != null ? result : value;
	}

	/**
	 * 如果Key不存在则创建，返回最后存储在Map中的Value.
	 * 
	 * 如果创建对象有一定成本, 直接使用PutIfAbsent可能重复浪费，则使用此类，传入回调的ConcurrentInitializer
	 * 
	 * @see org.apache.commons.lang3.concurrent.ConcurrentUtils#createIfAbsent(ConcurrentMap, Object,
	 * org.apache.commons.lang3.concurrent.ConcurrentInitializer)
	 */
	public static <K, V> V createIfAbsent(final ConcurrentMap<K, V> map, final K key, final ValueInitializer<V> init) {
		if (map == null || init == null) {
			return null;
		}

		final V value = map.get(key);
		if (value == null) {
			return putIfAbsentWithFinalValue(map, key, init.get());
		}
		return value;
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
	 * 同时初始化第一个元素
	 * 
	 * @see com.google.common.collect.Maps#newHashMap()
	 */
	public static <K, V> HashMap<K, V> newHashMap(final K key, final V value) {
		HashMap<K, V> map = new HashMap<K, V>();
		map.put(key, value);
		return map;
	}

	/**
	 * 根据等号左边的类型, 构造类型正确的HashMap.
	 * 
	 * 同时初始化元素.
	 */
	public static <K, V> HashMap<K, V> newHashMap(final K[] keys, final V[] values) {
		if (keys.length != values.length) {
			throw new IllegalArgumentException(
					"keys.length is " + keys.length + " but values.length is " + values.length);
		}

		HashMap<K, V> map = new HashMap<K, V>();

		for (int i = 0; i < keys.length; i++) {
			map.put(keys[i], values[i]);
		}

		return map;
	}

	/**
	 * 根据等号左边的类型, 构造类型正确的HashMap.
	 * 
	 * 同时初始化元素.
	 */
	public static <K, V> HashMap<K, V> newHashMap(final List<K> keys, final List<V> values) {
		if (keys.size() != values.size()) {
			throw new IllegalArgumentException("keys.size is " + keys.size() + " but values.size is " + values.size());
		}

		HashMap<K, V> map = new HashMap<K, V>();
		Iterator<K> keyIt = keys.iterator();
		Iterator<V> valueIt = values.iterator();

		while (keyIt.hasNext()) {
			map.put(keyIt.next(), valueIt.next());
		}

		return map;
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
	
	public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap() {
		return new ConcurrentHashMap<K, V>();
	}

	///////////////// from JDK Collections的常用构造函数 ///////////////////

	/**
	 * 返回一个空的结构特殊的Map，节约空间. 注意Map不可写.
	 * 
	 * @see java.util.Collections#emptyMap()
	 */
	public static final <K, V> Map<K, V> emptyMap() {
		return (Map<K, V>) Collections.EMPTY_MAP;
	}

	/**
	 * 如果map为null，转化为一个安全的空Map，注意Map不可写.
	 * 
	 * @see java.util.Collections#emptyMap()
	 */
	public static <K, V> Map<K, V> emptyMapIfNull(final Map<K, V> map) {
		return map == null ? (Map<K, V>) Collections.EMPTY_MAP : map;
	}

	/**
	 * 返回一个只含一个元素但结构特殊的Map，节约空间. 注意Map不可写.
	 * 
	 * @see java.util.Collections#singletonMap(Object, Object)
	 */
	public static <K, V> Map<K, V> singletonMap(final K key, final V value) {
		return Collections.singletonMap(key, value);
	}

	/**
	 * 返回包装后不可修改的Map
	 */
	public static <K, V> Map<K, V> unmodifiableMap(final Map<? extends K, ? extends V> m) {
		return Collections.unmodifiableMap(m);
	}

	/**
	 * 返回包装后不可修改的Map
	 */
	public static <K, V> SortedMap<K, V> unmodifiableSortedMap(final SortedMap<K, ? extends V> m) {
		return Collections.unmodifiableSortedMap(m);
	}

	/**
	 * from Common Lang
	 * 
	 * @see Maps#createIfAbsent(ConcurrentMap, Object, ValueInitializer)
	 */
	public interface ValueInitializer<T> {
		/**
		 * 创建对象
		 */
		T get();
	}
}
