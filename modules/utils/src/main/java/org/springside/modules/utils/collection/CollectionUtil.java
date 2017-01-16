/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.utils.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.annotations.Beta;

/**
 * 通用Collection的工具集
 * 
 * 通用函数没有针对数据结构进行优化，效率不高，所以没有太多的封装.
 * 
 * 关于List, Map, Queue, Set的特殊工具集，另见特定的Utils
 * 
 * 另JDK中缺少ComparableComparator和NullComparator，直到JDK8才补上。
 * 
 * 因此平时请使用guava的Ordering,fluentable的API更好用，可以设置nullFirst，nullLast,reverse
 * 
 * @see com.google.common.collect.Ordering
 */
@Beta
public abstract class CollectionUtil {

	/**
	 * 判断是否为空.
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return (collection == null) || collection.isEmpty();
	}

	/**
	 * 判断是否不为空.
	 */
	public static boolean isNotEmpty(Collection<?> collection) {
		return (collection != null) && !(collection.isEmpty());
	}

	/**
	 * 取得Collection的第一个元素，如果collection为空返回null.
	 */
	public static <T> T getFirst(Collection<T> collection) {
		if (isEmpty(collection)) {
			return null;
		}
		if (collection instanceof List) {
			return ((List<T>) collection).get(0);
		}
		return collection.iterator().next();
	}

	/**
	 * 获取Collection的最后一个元素，如果collection为空返回null.
	 */
	public static <T> T getLast(Collection<T> collection) {
		if (isEmpty(collection)) {
			return null;
		}

		// 当类型List时，直接取得最后一个元素.
		if (collection instanceof List) {
			List<T> list = (List<T>) collection;
			return list.get(list.size() - 1);
		}

		// 其他类型通过iterator滚动到最后一个元素.
		Iterator<T> iterator = collection.iterator();
		while (true) {
			T current = iterator.next();
			if (!iterator.hasNext()) {
				return current;
			}
		}
	}

	/**
	 * 返回无序集合中的最小值，使用元素默认排序
	 */
	public static <T extends Object & Comparable<? super T>> T min(Collection<? extends T> coll) {
		return Collections.min(coll);
	}

	/**
	 * 返回无序集合中的最小值
	 */
	public static <T> T min(Collection<? extends T> coll, Comparator<? super T> comp) {
		return Collections.min(coll, comp);
	}

	/**
	 * 返回无序集合中的最大值，使用元素默认排序
	 */
	public static <T extends Object & Comparable<? super T>> T max(Collection<? extends T> coll) {
		return Collections.max(coll);
	}

	/**
	 * 返回无序集合中的最大值
	 */
	public static <T> T max(Collection<? extends T> coll, Comparator<? super T> comp) {
		return Collections.max(coll, comp);
	}

	/**
	 * 返回无序集合中的最小值和最大值，使用元素默认排序
	 */
	public static <T extends Object & Comparable<? super T>> Pair<T, T> minAndMax(Collection<? extends T> coll) {
		Iterator<? extends T> i = coll.iterator();
		T minCandidate = i.next();
		T maxCandidate = minCandidate;

		while (i.hasNext()) {
			T next = i.next();
			if (next.compareTo(minCandidate) < 0) {
				minCandidate = next;
			} else if (next.compareTo(maxCandidate) > 0) {
				maxCandidate = next;
			}
		}
		return new ImmutablePair<T, T>(minCandidate, maxCandidate);
	}

	/**
	 * 返回无序集合中的最小值和最大值
	 */
	public static <T> Pair<T, T> minAndMax(Collection<? extends T> coll, Comparator<? super T> comp) {

		Iterator<? extends T> i = coll.iterator();
		T minCandidate = i.next();
		T maxCandidate = minCandidate;

		while (i.hasNext()) {
			T next = i.next();
			if (comp.compare(next, minCandidate) < 0) {
				minCandidate = next;
			} else if (comp.compare(next, maxCandidate) > 0) {
				maxCandidate = next;
			}
		}

		return new ImmutablePair<T, T>(minCandidate, maxCandidate);
	}
}
