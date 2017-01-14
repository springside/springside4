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

import org.springside.modules.utils.collection.type.NaturalOrderComparator;

import com.google.common.annotations.Beta;

/**
 * 通用Collection的工具集
 * 
 * 通用函数没有针对数据结构进行优化，效率不高，所以没有太多的封装.
 * 
 * 关于List, Map, Queue, Set的特殊工具集，另见特定的Utils
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

	///// 排序 ////

	/**
	 * 按Comparable默认顺序排序的Comparetor
	 */
	@SuppressWarnings("unchecked")
	public static <T> Comparator<T> naturalOrderComparator() {
		return NaturalOrderComparator.INSTANCE;
	}

	/**
	 * 按Comparable默认顺序倒序的Comparator
	 */
	public static <T> Comparator<T> reverseNaturalOrderComparator() {
		return Collections.reverseOrder(null);
	}

	/**
	 * 将Comparator倒序的Comparator
	 */
	public static <T> Comparator<T> reverseComparator(Comparator<T> cmp) {
		return Collections.reverseOrder(cmp);
	}
}
