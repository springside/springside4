/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.demos.utilities.collection;

import static org.assertj.core.api.Assertions.*;

import java.util.Collection;

import org.junit.Test;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

public class AdvancedMapDemo {

	/**
	 * Guava Multimap, 同一个key可以有多个value的Map.
	 */
	@Test
	public void multiMap() {
		Multimap<Integer, String> multimap = HashMultimap.create();
		multimap.put(1, "a");
		multimap.put(2, "b");
		multimap.put(3, "c");
		multimap.put(1, "a2");

		// 取出key=1的两个值
		Collection<String> values = multimap.get(1);
		assertThat(values).hasSize(2);

		// size是4不是3
		assertThat(multimap.size()).isEqualTo(4);

		// 删除其中一个值
		multimap.remove(1, "a");
		assertThat(multimap.get(1).size()).isEqualTo(1);

	}

	/**
	 * Guava BiMap，可随时调转Key与Value.
	 */
	@Test
	public void biMap() {
		BiMap<Integer, String> biMap = HashBiMap.create();
		BiMap<String, Integer> inverseMap = biMap.inverse();

		// 在biMap这边插入，在inserserMap可以用value取到key。
		biMap.put(1, "a");
		biMap.put(2, "b");
		assertThat(inverseMap.get("b")).isEqualTo(2);

		// 在inverseMap这边插入，在bimap这边也能用value取到key。
		inverseMap.put("c", 3);
		assertThat(biMap.get(3)).isEqualTo("c");

		// value也必须唯一，如果将key 4的value强行设为"a", 原来的key 1消失
		biMap.forcePut(4, "a");
		assertThat(biMap.containsKey(1)).isFalse();
	}

	/*
	 * Guava Table, 等于有兩个key的Map，可用来替代Map<String,Map<String,Object>
	 */
	@Test
	public void table() {
		Table<Integer, String, String> table = HashBasedTable.create();
		table.put(1, "a", "1a");
		table.put(1, "b", "1b");
		table.put(2, "a", "2a");
		table.put(2, "b", "2b");
		// 取单元格
		assertThat(table.get(2, "a")).isEqualTo("2a");
		// 取row
		assertThat(table.row(1)).contains(entry("a", "1a"), entry("b", "1b"));
	}

}
