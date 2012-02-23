package org.springside.examples.showcase.utilities.collection;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

public class AdvancedCollectionTypeDemo {

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

		Collection<String> values = multimap.get(1);
		assertEquals(2, values.size());
	}
}
