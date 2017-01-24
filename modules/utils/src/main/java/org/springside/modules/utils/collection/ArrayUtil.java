package org.springside.modules.utils.collection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springside.modules.utils.base.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

/**
 * 数组工具类.
 * 
 * JDK Arrays的其他函数，如sort(), toString() 请直接调用
 * 
 * Common Lang ArrayUtils的其他函数，如subarray(),reverse(),indeOf(), 请直接调用
 */
public class ArrayUtil {

	/**
	 * 将传入的数组乱序
	 */
	public static <T> T[] shuffle(T[] array) {
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

	/**
	 * 添加元素到数组头，没有银弹，复制扩容.
	 */
	public static <T> T[] concat(@Nullable T element, T[] array) {
		return ObjectArrays.concat(element, array);
	}

	/**
	 * 添加元素到数组末尾，没有银弹，复制扩容.
	 */
	public static <T> T[] concat(T[] array, @Nullable T element) {
		return ObjectArrays.concat(array, element);
	}

	/**
	 * 传入类型与大小创建数组.
	 */
	public static <T> T[] newArray(Class<T> type, int length) {
		return (T[]) Array.newInstance(type, length);
	}

	/**
	 * list.toArray() 返回的是Object[] 如果要有类型的数组话，就要使用list.toArray(new String[list.size()])，这里对调用做了简化
	 */
	public static <T> T[] toArray(List<T> list, Class<T> type) {
		return list.toArray((T[]) Array.newInstance(type, list.size()));
	}

	////////////////// guava Array向List的转换 ///////////

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
