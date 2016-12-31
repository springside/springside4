package org.springside.modules.utils.collection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;

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
	public static boolean isEmpty(Map<?, ?> map) {
		return (map == null) || map.isEmpty();
	}

	/**
	 * 判断是否为空.
	 */
	public static boolean isNotEmpty(Map<?, ?> map) {
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
	public static <K, V> HashMap<K, V> newHashMap(K key, V value) {
		HashMap<K, V> map =  new HashMap<K, V>();
		map.put(key, value);
		return map;
	}
	
	/**
	 * 根据等号左边的类型, 构造类型正确的HashMap. 
	 * 
	 * 同时初始化元素
	 * 
	 * @see com.google.common.collect.Maps#newHashMap()
	 */
	public static <K, V> HashMap<K, V> newHashMap(K[] keys, V[] values) {
		if(keys.length!=values.length){
			throw new IllegalArgumentException("keys.length is "+ keys.length +" but values.length is " + values.length);
		}
		
		HashMap<K, V> map =  new HashMap<K, V>();
		
		for(int i=0;i<keys.length;i++){
			map.put(keys[i], values[i]);
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

	///////////////// from JDK Collections的常用函数 ///////////////////

	/**
	 * 返回一个空的结构特殊的Map，节约空间. 注意Map不可写.
	 * 
	 * @see java.util.Collections#emptyMap()
	 */
	public static final <K, V> Map<K, V> emptyMap() {
		return Collections.EMPTY_MAP;
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
	public static <K, V> Map<K, V> singletonMap(K key, V value) {
		return Collections.singletonMap(key, value);
	}

	

	/**
	 * 返回包装后不可修改的Map
	 */
	public static <K, V> Map<K, V> unmodifiableMap(Map<? extends K, ? extends V> m) {
		return Collections.unmodifiableMap(m);
	}

	/**
	 * 返回包装后不可修改的Map
	 */
	public static <K, V> SortedMap<K, V> unmodifiableSortedMap(SortedMap<K, ? extends V> m) {
		return Collections.unmodifiableSortedMap(m);
	}
}
