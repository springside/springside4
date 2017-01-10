package org.springside.modules.utils.collection.type;

import java.util.Comparator;

/**
 * 又一个莫名奇妙缺失的类，如果一定要使用Comparator，不能使用Comparable对象自身的compareTo()函数时使用本类
 * 
 * 直接使用其INSTANCE即可.
 * 
 * @author calvin
 */
public class ComparableComparator<T extends Comparable<T>> implements Comparator<T> {

	@SuppressWarnings("rawtypes")
	public static final ComparableComparator INSTANCE = new ComparableComparator();

	@Override
	public int compare(T o1, T o2) {
		return o1.compareTo(o2);
	}

}
