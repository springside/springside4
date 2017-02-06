package org.springside.modules.utils.collection;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;
import org.springside.modules.utils.base.Platforms;
import org.springside.modules.utils.base.annotation.NotNull;
import org.springside.modules.utils.base.annotation.Nullable;
import org.springside.modules.utils.collection.type.primitive.IntObjectHashMap;
import org.springside.modules.utils.collection.type.primitive.LongObjectHashMap;
import org.springside.modules.utils.concurrent.jsr166e.ConcurrentHashMapV8;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeRangeMap;
import com.google.common.util.concurrent.AtomicLongMap;

/**
 * 关于Map的工具集合，
 * 
 * 1. 常用函数(如是否为空)
 * 
 * 2. 对于并发Map，增加putIfAbsent(返回最终值版), createIfAbsent这两个重要函数(from Common Lang)
 * 
 * 3. 便捷的构造函数(via guava,Java Collections，并增加了用数组，List等方式初始化Map的函数)
 * 
 * 4. 特殊的类型，包括WeakConcurrentHashMap, IntObjectHashMap, MapCounter, MultiKeyMap, RangeMap
 * 
 * 
 * 参考文章：《高性能场景下，Map家族的优化使用建议》 http://calvin1978.blogcn.com/articles/hashmap.html
 * 
 * @author calvin
 */
@SuppressWarnings("unchecked")
public class MapUtil {

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
	public static <K, V> V putIfAbsentWithFinalValue(@NotNull final ConcurrentMap<K, V> map, final K key,
			final V value) {
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
	public static <K, V> V createIfAbsent(@NotNull final ConcurrentMap<K, V> map, final K key,
			@NotNull final ValueCreator<? extends V> creator) {
		final V value = map.get(key);
		if (value == null) {
			return putIfAbsentWithFinalValue(map, key, creator.get());
		}
		return value;
	}

	/**
	 * 创建Value值的回调函数
	 * 
	 * @see MapUtil#createIfAbsent(ConcurrentMap, Object, ValueCreator)
	 */
	public interface ValueCreator<T> {
		/**
		 * 创建对象
		 */
		T get();
	}

	///////////////// from Guava的构造函数///////////////////

	/**
	 * 根据等号左边的类型, 构造类型正确的HashMap.
	 * 
	 * 未初始化数组大小, 默认为16个桶.
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
	 * 加载因子也是HashMap中减少Hash冲突的重要一环，如果读写频繁，总记录数不多的Map，可以比默认值0.75进一步降低，建议0.5
	 * 
	 * @see com.google.common.collect.Maps#newHashMap(int)
	 */
	public static <K, V> HashMap<K, V> newHashMapWithCapacity(int expectedSize, float loadFactor) {
		int finalSize = (int) ((double) expectedSize / loadFactor + 1.0F);
		return new HashMap<K, V>(finalSize, loadFactor);
	}

	/**
	 * 根据等号左边的类型, 构造类型正确的HashMap.
	 * 
	 * 同时初始化第一个元素
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
	public static <K, V> HashMap<K, V> newHashMap(@NotNull final K[] keys, @NotNull final V[] values) {
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
	public static <K, V> HashMap<K, V> newHashMap(@NotNull final List<K> keys, @NotNull final List<V> values) {
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
	 * 根据等号左边的类型，构造类型正确的TreeMap.
	 * 
	 * @see com.google.common.collect.Maps#newTreeMap()
	 */
	@SuppressWarnings("rawtypes")
	public static <K extends Comparable, V> TreeMap<K, V> newSortedMap() {
		return new TreeMap<K, V>();
	}

	/**
	 * 根据等号左边的类型，构造类型正确的TreeMap.
	 * 
	 * @see com.google.common.collect.Maps#newTreeMap(Comparator)
	 */
	public static <C, K extends C, V> TreeMap<K, V> newSortedMap(@Nullable Comparator<C> comparator) {
		return Maps.newTreeMap(comparator);
	}

	/**
	 * 相比HashMap，当key是枚举类时, 性能与空间占用俱佳.
	 */
	public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(@NotNull Class<K> type) {
		return new EnumMap<K, V>(Preconditions.checkNotNull(type));
	}

	/**
	 * JDK8下，ConcurrentHashMap已不再需要设置loadFactor, concurrencyLevel和initialCapacity.
	 * 
	 * 如果JDK8，使用原生ConcurrentHashMap，否则使用移植版
	 */
	public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap() {
		if (Platforms.IS_ATLEASET_JAVA8) {
			return new ConcurrentHashMap<K, V>();
		} else {
			return new ConcurrentHashMapV8<K, V>();
		}
	}

	/**
	 * 根据等号左边的类型，构造类型正确的ConcurrentSkipListMap.
	 */
	public static <K, V> ConcurrentSkipListMap<K, V> newConcurrentSortedMap() {
		return new ConcurrentSkipListMap<K, V>();
	}

	///////////////// from Guava的特别Map //////////////////////

	/**
	 * 创建Key为弱引用的ConcurrentMap，Key对象可被回收.
	 * 
	 * JDK没有WeakHashMap的并发实现, 由Guava提供
	 */
	public static <K, V> ConcurrentMap<K, V> createWeakKeyConcurrentHashMap(int initialCapacity, int concurrencyLevel) {
		return new MapMaker().weakKeys().initialCapacity(initialCapacity).concurrencyLevel(concurrencyLevel).makeMap();
	}

	/**
	 * 创建Value为弱引用的ConcurrentMap，Value对象可被回收.
	 * 
	 * JDK没有WeakHashMap的并发实现, 由Guava提供
	 */
	public static <K, V> ConcurrentMap<K, V> createWeakValueConcurrentHashMap(int initialCapacity,
			int concurrencyLevel) {
		return new MapMaker().weakValues().initialCapacity(initialCapacity).concurrencyLevel(concurrencyLevel)
				.makeMap();
	}

	/**
	 * 创建移植自Netty的key为int的优化HashMap
	 * 
	 * @param initialCapacity 建议为16
	 * @param loadFactor 建议为0.5
	 */
	public static <V> IntObjectHashMap<V> createIntObjectHashMap(int initialCapacity, float loadFactor) {
		return new IntObjectHashMap<V>(initialCapacity, loadFactor);
	}

	/**
	 * 创建移植自Netty的key为long的优化HashMap
	 * 
	 * @param initialCapacity 建议为16
	 * @param loadFactor 建议为0.5
	 */
	public static <V> LongObjectHashMap<V> createLongObjectHashMap(int initialCapacity, float loadFactor) {
		return new LongObjectHashMap<V>(initialCapacity, loadFactor);
	}

	/**
	 * 创建值为可更改的Integer的HashMap. 可更改的Integer在更改时不需要重新创建Integer对象，节约了内存
	 * 
	 * @param initialCapacity 建议为16
	 * @param loadFactor 建议为0.5
	 */
	public static <K> HashMap<K, MutableInt> createMutableIntValueHashMap(int initialCapacity, float loadFactor) {
		return new HashMap<K, MutableInt>(initialCapacity, loadFactor);
	}

	/**
	 * 创建值为可更改的Long的HashMap. 可更改的Long在更改时不需要重新创建Long对象，节约了内存
	 * 
	 * @param initialCapacity 建议为16
	 * @param loadFactor 建议为0.5
	 */
	public static <K> HashMap<K, MutableLong> createMutableLongValueHashMap(int initialCapacity, float loadFactor) {
		return new HashMap<K, MutableLong>(initialCapacity, loadFactor);
	}

	/**
	 * 以Guava的AtomicLongMap，实现线程安全的HashMap<E,AtomicLong>结构的Counter
	 */
	public static <E> AtomicLongMap<E> createConcurrentMapCounter() {
		return AtomicLongMap.create();
	}

	/**
	 * 以Guava的MultiSet，实现线程安全的HashMap<E,Integer>结构的Counter
	 */
	public static <E> ConcurrentHashMultiset<E> createConcurrentMapCounter(Iterable<? extends E> elements) {
		return ConcurrentHashMultiset.create(elements);
	}

	/**
	 * 以Guava的MultiMap，实现的HashMap<E,List<V>>结构的一个Key对应多个值的map.
	 * 
	 * 注意非线程安全, MultiMap无线程安全的实现.
	 * 
	 * 另有其他结构存储values的MultiMap，请自行参考MultimapBuilder使用.
	 * 
	 * @param expectedKeys 默认为16
	 * @param expectedValuesPerKey 默认为3
	 */
	public static <K, V> ArrayListMultimap<K, V> createListValueMap(int expectedKeys, int expectedValuesPerKey) {
		return ArrayListMultimap.create(expectedKeys, expectedValuesPerKey);
	}

	/**
	 * 以Guava的MultiMap，实现的HashMap<E,TreeSet<V>>结构的一个Key对应多个值的map.
	 * 
	 * 注意非线程安全, MultiMap无线程安全的实现.
	 * 
	 * 另有其他结构存储values的MultiMap，请自行参考MultimapBuilder使用.
	 */
	public static <K, V extends Comparable> SortedSetMultimap<K, V> createSortedSetValueMap() {
		return MultimapBuilder.hashKeys().treeSetValues().build();
	}

	/**
	 * 以Guava的MultiMap，实现的HashMap<E,TreeSet<V>>结构的一个Key对应多个值的map.
	 * 
	 * 注意非线程安全, MultiMap无线程安全的实现.
	 * 
	 * 另有其他结构存储values的MultiMap，请自行参考MultimapBuilder使用.
	 */
	public static <K, V> SortedSetMultimap<K, V> createSortedSetValueMap(Comparator<V> comparator) {
		return (SortedSetMultimap<K, V>) MultimapBuilder.hashKeys().treeSetValues(comparator);
	}

	/**
	 * 以Guava TreeRangeMap实现的, 一段范围的Key指向同一个Value的Map
	 */
	@SuppressWarnings("rawtypes")
	public static <K extends Comparable, V> TreeRangeMap<K, V> createRangeMap() {
		return TreeRangeMap.create();
	}

	///////////////// from JDK Collections的常用构造函数 ///////////////////

	/**
	 * 返回一个空的结构特殊的Map，节约空间.
	 * 
	 * 注意返回的Map不可写, 写入会抛出UnsupportedOperationException.
	 * 
	 * @see java.util.Collections#emptyMap()
	 */
	public static final <K, V> Map<K, V> emptyMap() {
		return (Map<K, V>) Collections.EMPTY_MAP;
	}

	/**
	 * 如果map为null，转化为一个安全的空Map.
	 * 
	 * 注意返回的Map不可写, 写入会抛出UnsupportedOperationException.
	 * 
	 * @see java.util.Collections#emptyMap()
	 */
	public static <K, V> Map<K, V> emptyMapIfNull(final Map<K, V> map) {
		return map == null ? (Map<K, V>) Collections.EMPTY_MAP : map;
	}

	/**
	 * 返回一个只含一个元素但结构特殊的Map，节约空间.
	 * 
	 * 注意返回的Map不可写, 写入会抛出UnsupportedOperationException.
	 * 
	 * @see java.util.Collections#singletonMap(Object, Object)
	 */
	public static <K, V> Map<K, V> singletonMap(final K key, final V value) {
		return Collections.singletonMap(key, value);
	}

	/**
	 * 返回包装后不可修改的Map.
	 * 
	 * 如果尝试修改，会抛出UnsupportedOperationException
	 * 
	 * @see java.util.Collections#unmodifiableMap(Map)
	 */
	public static <K, V> Map<K, V> unmodifiableMap(final Map<? extends K, ? extends V> m) {
		return Collections.unmodifiableMap(m);
	}

	/**
	 * 返回包装后不可修改的有序Map.
	 * 
	 * @see java.util.Collections#unmodifiableSortedMap(SortedMap)
	 */
	public static <K, V> SortedMap<K, V> unmodifiableSortedMap(final SortedMap<K, ? extends V> m) {
		return Collections.unmodifiableSortedMap(m);
	}

	//////// Map的集合操作 //////

	/**
	 * 对两个Map进行比较，返回MapDifference，然后各种妙用.
	 * 
	 * 包括key的差集，key的交集，以及key相同但value不同的元素。
	 */
	public static <K, V> MapDifference<K, V> difference(Map<? extends K, ? extends V> left,
			Map<? extends K, ? extends V> right) {
		return Maps.difference(left, right);
	}

}
