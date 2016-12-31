package org.springside.modules.utils.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

/**
 * 关于List的工具集合，
 * 
 * 对于Guava与Java Collections中的常用函数，直接引用.
 * 
 * 对于Apache Commons Collection中的函数，直接移植避免引入依赖.
 * 
 * @author calvin
 */
@SuppressWarnings("unchecked")
public class Lists {

	/**
	 * 判断是否为空.
	 */
	public static boolean isEmpty(List<?> list) {
		return (list == null) || list.isEmpty();
	}

	/**
	 * 判断是否不为空.
	 */
	public static boolean isNotEmpty(List<?> list) {
		return (list != null) && !(list.isEmpty());
	}

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

	///////////////// from Guava的构造函数///////////////////
	/**
	 * 根据等号左边的类型，构造类型正确的ArrayList.
	 * 
	 * @see com.google.common.collect.Lists#newArrayList()
	 */
	public static <T> ArrayList<T> newArrayList() {
		return new ArrayList<T>();
	}

	/**
	 * 根据等号左边的类型，构造类型正确的ArrayList, 并初始化元素.
	 * 
	 * @see com.google.common.collect.Lists#newArrayList(Object...)
	 */
	public static <T> ArrayList<T> newArrayList(T... elements) {
		return com.google.common.collect.Lists.newArrayList(elements);
	}

	/**
	 * 根据等号左边的类型，构造类型正确的ArrayList, 并初始化数组大小.
	 * 
	 * @see com.google.common.collect.Lists#newArrayListWithCapacity(int)
	 */
	public static <T> ArrayList<T> newArrayListWithCapacity(int initialArraySize) {
		return new ArrayList<T>(initialArraySize);
	}

	/**
	 * 根据等号左边的类型，构造类型正确的LinkedList.
	 * 
	 * @see com.google.common.collect.Lists#newLinkedList()
	 */
	public static <T> LinkedList<T> newLinkedList() {
		return new LinkedList<T>();
	}

	/**
	 * 根据等号左边的类型，构造类型正确的CopyOnWriteArrayList.
	 * 
	 * @see com.google.common.collect.Lists#newCopyOnWriteArrayList()
	 */
	public static <T> CopyOnWriteArrayList<T> newCopyOnWriteArrayList() {
		return new CopyOnWriteArrayList<T>();
	}

	/**
	 * 根据等号左边的类型，构造类型转换的CopyOnWriteArrayList, 并初始化元素.
	 */
	public static <T> CopyOnWriteArrayList<T> newCopyOnWriteArrayList(T... elements) {
		return new CopyOnWriteArrayList<T>(elements);
	}

	///////////////// from JDK Collections的常用函数 ///////////////////

	/**
	 * 返回一个空的结构特殊的List，节约空间. 注意List不可写.
	 * 
	 * @see java.util.Collections#emptyList()
	 */
	public static final <T> List<T> emptyList() {
		return Collections.EMPTY_LIST;
	}

	/**
	 * 返回一个只含一个元素但结构特殊的List，节约空间. 注意List不可写.
	 * 
	 * @see java.util.Collections#singleton(Object)
	 */
	public static <T> List<T> singletonList(T o) {
		return Collections.singletonList(o);
	}

	/**
	 * 如果list为null，转化为一个安全的空List. 注意List不可写.
	 * 
	 * @see java.util.Collections#emptyList()
	 */
	public static <T> List<T> emptyListIfNull(final List<T> list) {
		return list == null ? (List<T>) Collections.EMPTY_LIST : list;
	}
	
	/**
	 * 返回包装后不可修改的List
	 */
	 public static <T> List<T> unmodifiableList(List<? extends T> list) {
		 return Collections.unmodifiableList(list);
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

	////////////////// ArrayList 与 Array的双向转换 ///////////

	/**
	 * 将List转换为数组.
	 * 
	 * 其中List的实现均以良好的初始化数组大小，不需要使用list.toArray(T[])
	 */
	public static <T> T[] toArray(List<T> list) {
		return (T[]) list.toArray();
	}

	/**
	 * 将数组转换为List.
	 * 
	 * 注意转换后的List不能写入.
	 * 
	 * @see java.util.Arrays#asList(Object...)
	 */
	public static <T> List<T> asList(T... a) {
		return Arrays.asList(a);
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

	///////////////// 集合运算 ///////////////////

	/**
	 * 比较两个List中的每个元素是否相等.
	 * 
	 * from Apache Common Collections4
	 */
	public static boolean isEqual(final List<?> list1, final List<?> list2) {
		if (list1 == list2) {
			return true;
		}
		if (list1 == null || list2 == null || list1.size() != list2.size()) {
			return false;
		}

		final Iterator<?> it1 = list1.iterator();
		final Iterator<?> it2 = list2.iterator();
		Object obj1 = null;
		Object obj2 = null;

		while (it1.hasNext() && it2.hasNext()) {
			obj1 = it1.next();
			obj2 = it2.next();

			if (!(obj1 == null ? obj2 == null : obj1.equals(obj2))) {
				return false;
			}
		}

		return !(it1.hasNext() || it2.hasNext());
	}

	/**
	 * 返回a+b的新List, 与Collections中的实现一样.
	 */
	public static <T> List<T> union(final List<T> list1, final List<T> list2) {
		List<T> result = new ArrayList<T>(list1);
		result.addAll(list2);
		return result;
	}

	/**
	 * 返回a-b的新List, 与Collections中的实现一样.
	 * 
	 * 与removeAll()的区别是考虑了相同元素在集合里的数量，比如list1有两个A，list2有一个A，结果剩下一个A.
	 */
	public static <T> List<T> subtract(final List<T> list1, final List<T> list2) {
		List<T> list = new ArrayList<T>(list1);
		for (T element : list2) {
			list.remove(element);
		}

		return list;
	}

	/**
	 * 返回a与b的交集的新List.
	 * 
	 * 针对List作了优化，from Apache Common Collection4
	 */
	public static <T> List<T> intersection(List<T> list1, List<T> list2) {
		final List<T> result = new ArrayList<T>();

		List<? extends T> smaller = list1;
		List<? extends T> larger = list2;
		if (list1.size() > list2.size()) {
			smaller = list2;
			larger = list1;
		}

		final HashSet<T> hashSet = new HashSet<T>(smaller);

		for (final T e : larger) {
			if (hashSet.contains(e)) {
				result.add(e);
				hashSet.remove(e);
			}
		}
		return result;
	}
}
