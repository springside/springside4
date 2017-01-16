package org.springside.modules.utils.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

/**
 * 数组工具类.
 * 
 * Arrays的其他函数，如sort(), toString() 请直接调用
 */
public class ArrayUtil {

	/**
	 * 将传入的数组乱序
	 */
	public final static <T> T[] shuffle(T[] array) {
		List<T> list = new ArrayList<T>(array.length);
		Collections.addAll(list, array);
		Collections.shuffle(list);
		return list.toArray(array);
	}

	/**
	 * 将传入的数组乱序
	 */
	public static <T> T[] shuffle(T[] array, Random random) {
		List<T> list = new ArrayList<T>(Arrays.asList(array));
		Collections.shuffle(list, random);
		return list.toArray(array);
	}

	
	////////////////// guava一些Array向List的转换 ///////////

	/**
	 * 原版将数组转换为List.
	 * 
	 * 注意转换后的List不能写入, 否则抛出UnsupportedOperationException
	 * 
	 * @see java.util.Arrays#asList(Object...)
	 */
	public static <T> List<T> asList(T... a) {
		return Arrays.asList(a);
	}

	/**
	 * 一个独立元素＋一个数组组成新的list，只是一个View，不复制数组内容，而且独立元素在最前.
	 *
	 * 
	 * 注意转换后的List不能写入, 否则抛出UnsupportedOperationException
	 * 
	 * @see com.google.common.collect.Lists#asList(Object, Object[])
	 */
	public static <E> List<E> asList(E first, E[] rest) {
		return Lists.asList(first, rest);
	}

	/**
	 * Arrays.asList()的加强版, 返回一个底层为原始类型int的List
	 * 
	 * 与保存Integer相比节约空间，同时只在读取数据时AutoBoxing.
	 * 
	 * @see java.util.Arrays#asList(Object...)
	 * @see com.google.common.primitives.Ints#asList(int...)
	 * 
	 */
	public static List<Integer> intAsList(int... backingArray) {
		return Ints.asList(backingArray);
	}

	/**
	 * Arrays.asList()的加强版, 返回一个底层为原始类型long的List
	 * 
	 * 与保存Long相比节约空间，同时只在读取数据时AutoBoxing.
	 * 
	 * @see java.util.Arrays#asList(Object...)
	 * @see com.google.common.primitives.Longs#asList(long...)
	 */
	public static List<Long> longAsList(long... backingArray) {
		return Longs.asList(backingArray);
	}

	/**
	 * Arrays.asList()的加强版, 返回一个底层为原始类型double的List
	 * 
	 * 与保存Double相比节约空间，同时也避免了AutoBoxing.
	 * 
	 * @see java.util.Arrays#asList(Object...)
	 * @see com.google.common.primitives.Doubles#asList(double...)
	 */
	public static List<Double> doubleAsList(double... backingArray) {
		return Doubles.asList(backingArray);
	}

}
