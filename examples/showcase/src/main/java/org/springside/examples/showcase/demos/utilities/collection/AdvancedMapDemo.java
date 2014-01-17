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
	 * Guava的同一个key可以有多个value的Map.
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
	 * Guava的BiMap，可随时调转Key与Value.
	 */
	@Test
	public void biMap() {
		BiMap<Integer, String> bimap = HashBiMap.create();
		BiMap<String, Integer> inverseMap = bimap.inverse();

		bimap.put(1, "a");
		bimap.put(2, "b");
		assertThat(inverseMap.get("b")).isEqualTo(2);

		inverseMap.put("c", 3);
		assertThat(bimap.get(3)).isEqualTo("c");
	}

	/*
	 * Guava的Table等于有兩个key的Map，可用来替代Map<String,Map<String,Object>
	 */
	@Test
	public void table() {
		Table<Integer, String, String> table = HashBasedTable.create();
		table.put(1, "a", "1a");
		table.put(1, "b", "1b");
		table.put(2, "a", "2a");
		table.put(2, "b", "2b");

		assertThat(table.get(2, "a")).isEqualTo("2a");
	}

}
