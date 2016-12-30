package org.springside.modules.utils.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

@SuppressWarnings("unchecked")
public class Lists {

	/**
	 * 判断是否为空.
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(List list) {
		return (list == null) || list.isEmpty();
	}

	/**
	 * 判断是否不为空.
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isNotEmpty(List list) {
		return (list != null) && !(list.isEmpty());
	}

	///////////////// from Guava ///////////////////
	/**
	 * 根据等号左边的类型，构造类型转换的ArrayList.
	 * 
	 * @see com.google.common.collect.Lists#newArrayList()
	 */
	public static <T> ArrayList<T> newArrayList() {
		return new ArrayList<T>();
	}

	/**
	 * 根据等号左边的类型，构造类型转换的ArrayList, 并初始化元素.
	 * 
	 * @see com.google.common.collect.Lists#newArrayList(Object...)
	 */
	public static <T> ArrayList<T> newArrayList(T... elements) {
		return com.google.common.collect.Lists.newArrayList(elements);
	}

	/**
	 * 根据等号左边的类型，构造类型转换的ArrayList, 并初始化数组大小.
	 * 
	 * @see com.google.common.collect.Lists#newArrayListWithCapacity(int)
	 */
	public static <T> ArrayList<T> newArrayListWithCapacity(int initialArraySize) {
		return new ArrayList<T>(initialArraySize);
	}

	/**
	 * Arrays.asList()的加强版, 返回一个底层由原始类型int保存的List, 与保存Integer相比节约空间.
	 * 
	 * @see java.util.Arrays#asList(Object...)
	 * @see com.google.common.primitives.Ints#asList(int...)
	 * 
	 */
	public static List<Integer> asList(int... backingArray) {
		return Ints.asList(backingArray);
	}

	/**
	 * Arrays.asList()的加强版, 返回一个底层由原始类型long保存的List, 与保存Long相比节约空间.
	 * 
	 * @see java.util.Arrays#asList(Object...)
	 * @see com.google.common.primitives.Longs#asList(long...)
	 */
	public static List<Long> asList(long... backingArray) {
		return Longs.asList(backingArray);
	}

	/**
	 * Arrays.asList()的加强版, 返回一个底层由原始类型double保存的Double, 与保存Double相比节约空间.
	 * 
	 * @see java.util.Arrays#asList(Object...)
	 * @see com.google.common.primitives.Doubles#asList(double...)
	 */
	public static List<Double> asList(double... backingArray) {
		return Doubles.asList(backingArray);
	}

	///////////////// from JDK Collecgtions ///////////////////

	/**
	 * 返回一个空的结构特别的数组，节约空间. 注意数组不可写.
	 * 
	 * @see java.util.Collections#emptyList()
	 */
	public static final <T> List<T> emptyList() {
		return (List<T>) Collections.EMPTY_LIST;
	}

	/**
	 * 返回一个只含一个元素但结构特别的数组，节约空间. 注意数组不可写.
	 * 
	 * @see java.util.Collections#singleton(Object)
	 */
	public static <T> List<T> singletonList(T o) {
		return Collections.singletonList(o);
	}

	/**
	 * 排序, 采用JDK认为最优的排序算法.
	 * 
	 * @see java.util.Collections#sort(List)
	 */
	public static <T extends Comparable<? super T>> void sort(List<T> list) {
		list.sort(null);
	}

	/**
	 * 排序, 采用JDK认为最优的排序算法.
	 * 
	 * @see java.util.Collections#sort(List, Comparator)
	 */
	public static <T> void sort(List<T> list, Comparator<? super T> c) {
		list.sort(c);
	}

	/**
	 * 二分查找.
	 * 
	 * @see java.util.Collections#binarySearch(List, Object)
	 */
	public static <T> int binarySearch(List<? extends Comparable<? super T>> list, T key) {
		return Collections.binarySearch(list, key);
	}

	/**
	 * 二分查找.
	 * 
	 * @see java.util.Collections#binarySearch(List, Object, Comparator)
	 */
	public static <T> int binarySearch(List<? extends T> list, T key, Comparator<? super T> c) {
		return Collections.binarySearch(list, key, c);
	}

	/**
	 * 随机乱序，使用默认的Random.
	 * 
	 * @see java.util.Collections#shuffle(List)
	 */
	public static void shuffle(List<?> list) {
		Collections.shuffle(list);
	}

	/**
	 * 随机乱序，使用传入的Random.
	 * 
	 * @see java.util.Collections#shuffle(List, Random)
	 */
	public static void shuffle(List<?> list, Random rnd) {
		Collections.shuffle(list, rnd);
	}

	///////////////// from Apache Common Collection ///////////////////

	/**
	 * 获取第一个元素, 如果List为空返回 null.
	 */
	public static <T> T getFirst(List<T> list) {
		if (isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 获取最后一个元素，如果List为空返回null.
	 */
	public static <T> T getLast(List<T> list) {
		if (isEmpty(list)) {
			return null;
		}

		return list.get(list.size() - 1);
	}
}
