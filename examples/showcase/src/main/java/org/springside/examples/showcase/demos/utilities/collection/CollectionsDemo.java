/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.demos.utilities.collection;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springside.examples.showcase.entity.User;
import org.springside.modules.utils.Collections3;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 演示SpringSide Collections3(替代Apache Collections)和Guava 的Collection 如何简化Collection操作.
 * 
 * @author calvin
 */
public class CollectionsDemo {

	@Test
	public void initCollection() {
		// 无需在等号右边重新定义泛型的创建ArrayList
		List<String> list = Lists.newArrayList();
		// 创建的同时初始化数据
		List<String> list2 = Lists.newArrayList("a", "b", "c");

		// 无需在等号右边重新定义泛型的创建HashMap
		Map<String, ? extends User> map = Maps.newHashMap();
		// 创建Map的同时初始化值，不过这个map是不可修改的，主要用于编写测试用例。
		Map<Integer, String> unmodifiedMap = ImmutableMap.of(1, "foo", 2, "bar");
	}

	@Test
	public void operation() {
		List<String> list = Lists.newArrayList("a", "b", "c");
		List<String> list2 = Lists.newArrayList("a", "b");

		// nullsafe的判断是否为空
		assertThat(Collections3.isEmpty(list)).isFalse();

		// 获取最后一个
		assertThat(Collections3.getLast(list)).isEqualTo("c");

		// list+list2的新List
		List result = Collections3.union(list, list2);
		assertThat(result).containsSequence("a", "b", "c", "a", "b");

		// list-list2的新List
		result = Collections3.subtract(list, list2);
		assertThat(result).containsOnly("c");

		// list与list2的交集的新List
		result = Collections3.intersection(list, list2);
		assertThat(result).containsOnly("a", "b");
	}
}
