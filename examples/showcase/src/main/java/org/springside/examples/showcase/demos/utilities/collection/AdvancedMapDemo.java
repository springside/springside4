package org.springside.examples.showcase.demos.utilities.collection;

import static org.junit.Assert.*;

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
		assertEquals(2, values.size());

		// size是4不是3
		assertEquals(4, multimap.size());

		// 删除其中一个值
		multimap.remove(1, "a");
		assertEquals(1, multimap.get(1).size());

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
		assertEquals(Integer.valueOf(2), inverseMap.get("b"));

		inverseMap.put("c", 3);
		assertEquals("c", bimap.get(3));
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

		assertEquals("2a", table.get(2, "a"));
	}

}
