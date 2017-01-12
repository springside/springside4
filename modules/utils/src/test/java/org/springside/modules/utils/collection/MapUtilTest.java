package org.springside.modules.utils.collection;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.junit.Test;
import org.springside.modules.utils.collection.type.ComparableComparator;

public class MapUtilTest {

	@Test
	public void guavaBuildSet() {
		HashMap<String, Integer> map1 = MapUtil.newHashMap();

		HashMap<String, Integer> map2 = MapUtil.newHashMapWithCapacity(10);
		map2 = MapUtil.newHashMapWithCapacity(10, 0.5f);

		HashMap<String, Integer> map3 = MapUtil.newHashMap("1", 1);
		assertThat(map3).hasSize(1).containsEntry("1", 1);

		HashMap<String, Integer> map4 = MapUtil.newHashMap(new String[] { "1", "2" }, new Integer[] { 1, 2 });
		assertThat(map4).hasSize(2).containsEntry("1", 1).containsEntry("2", 2);

		HashMap<String, Integer> map5 = MapUtil.newHashMap(ListUtil.asList("1", "2", "3"), ListUtil.asList(1, 2, 3));
		assertThat(map5).hasSize(3).containsEntry("1", 1).containsEntry("2", 2).containsEntry("3", 3);

		TreeMap<String, Integer> map6 = MapUtil.newNavigableMap();

		TreeMap<String, Integer> map7= MapUtil.newNavigableMap(ComparableComparator.INSTANCE);

		ConcurrentHashMap map8 = MapUtil.newConcurrentHashMap();
		ConcurrentSkipListMap map9 = MapUtil.newConcurrentNavigableMap();
	}

}
