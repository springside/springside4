package org.springside.modules.utils.collection;

import java.util.Collections;
import java.util.Comparator;

/**
 * 关于Comparator的工具集
 *
 */
public class ComparatorUtil {

	@SuppressWarnings("rawtypes")
	public static final ComparableComparator NATUAL = new ComparableComparator();

	/**
	 * 按Comparable默认顺序排序的Comparetor
	 */
	@SuppressWarnings("unchecked")
	public static <T> Comparator<T> natural() {
		return NATUAL;
	}

	/**
	 * 按Comparable默认顺序倒序的Comparator
	 */
	public static <T> Comparator<T> reverseNatural() {
		return Collections.reverseOrder();
	}

	/**
	 * 按Comparable默认顺序，Null排在前面的Comparator
	 */
	@SuppressWarnings({ "unchecked" })
	public static <T> Comparator<T> nullFirstNatural() {
		return new NullComparator(true, NATUAL);
	}

	/**
	 * 按Comparable默认顺序，Null排在后面的Comparator
	 */
	@SuppressWarnings("unchecked")
	public static <T> Comparator<T> nullLastNatural() {
		return new NullComparator(false, NATUAL);
	}

	/**
	 * 将Comparator倒序的Comparator
	 */
	public static <T> Comparator<T> reverse(Comparator<T> cmp) {
		return Collections.reverseOrder(cmp);
	}

	/**
	 * 将null排在前面的Comparator
	 */
	public static <T> Comparator<T> nullFirst(Comparator<? super T> comparator) {
		return new NullComparator(true, comparator);
	}
	
	/**
	 * 将null排在后面的Comparator
	 */
	public static <T> Comparator<T> nullLast(Comparator<? super T> comparator) {
		return new NullComparator(false, comparator);
	}

	/**
	 * 又一个莫名奇妙缺失的类, 直到JDK8才补回来的类
	 * 
	 * 如果一定要使用Comparator，不能使用Comparable对象自身的compareTo()函数时使用本类
	 * 
	 * 直接使用其INSTANCE即可.
	 * 
	 * @author calvin
	 */
	public static class ComparableComparator<T extends Comparable<T>> implements Comparator<T> {

		@Override
		public int compare(T o1, T o2) {
			return o1.compareTo(o2);
		}
	}

	/**
	 * 另一个JDK8才补回来的Comparator
	 */
	public static class NullComparator<T> implements Comparator<T> {

		private final boolean nullFirst;
		private final Comparator<T> real;

		NullComparator(boolean nullFirst, Comparator<? super T> real) {
			this.nullFirst = nullFirst;
			this.real = (Comparator<T>) real;
		}

		@Override
		public int compare(T a, T b) {
			if (a == null) {
				return (b == null) ? 0 : (nullFirst ? -1 : 1);
			} else if (b == null) {
				return nullFirst ? 1 : -1;
			} else {
				return (real == null) ? 0 : real.compare(a, b);
			}
		}
	}
}
