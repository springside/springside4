package org.springside.modules.utils.collection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.springside.modules.utils.collection.extend.ConcurrentHashSet;

import com.google.common.collect.Sets;

public class SetUtil {

	public <T> HashSet<T> newHashSet() {
		return new HashSet<T>();
	}

	/**
	 * 创建HashSet并设置初始大小，因为HashSet内部是HashMap，会计算LoadFactor后的真实大小.
	 */
	public <T> HashSet<T> newHashSetWithExpectedSize(int expectedSize) {
		return Sets.newHashSetWithExpectedSize(expectedSize);
	}

	public static <E extends Comparable> TreeSet<E> newSortedSet() {
		return new TreeSet<E>();
	}

	public <T> ConcurrentHashSet<T> newConcurrentHashSet() {
		return new ConcurrentHashSet<T>();
	}

	public static final <T> Set<T> emptySet() {
		return (Set<T>) Collections.EMPTY_SET;
	}

	//////////////// 集合函数/////////
	/**
	 * 返回set1, set2并集（在set1或set2的对象）的只读view，不复制产生新的Set对象.
	 */
	public static <E> Set<E> union(final Set<? extends E> set1, final Set<? extends E> set2) {
		return Sets.union(set1, set2);
	}

	/**
	 * 返回set1, set2交集（同时在set1和set2的对象）的只读view，不复制产生新的Set对象.
	 */
	public static <E> Set<E> intersection(final Set<E> set1, final Set<?> set2) {
		return Sets.intersection(set1, set2);
	}

	/**
	 * 返回set1, set2差集（在set1，不在set2中的对象）的只读view，不复制产生新的Set对象.
	 */
	public static <E> Set<E> difference(final Set<E> set1, final Set<?> set2) {
		return Sets.difference(set1, set2);
	}
}
