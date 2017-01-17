package org.springside.modules.utils.collection;

import static org.assertj.core.api.Assertions.*;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.junit.Test;
import org.springside.modules.utils.collection.MapUtil.ValueCreator;

import com.google.common.collect.Ordering;

public class MapUtilTest {

	@Test
	public void generalMethod() {
		HashMap<String, Integer> map = MapUtil.newHashMap();
		assertThat(MapUtil.isEmpty(map)).isTrue();
		assertThat(MapUtil.isEmpty(null)).isTrue();
		assertThat(MapUtil.isNotEmpty(map)).isFalse();
		assertThat(MapUtil.isNotEmpty(null)).isFalse();

		map.put("haha", 1);
		assertThat(MapUtil.isEmpty(map)).isFalse();
		assertThat(MapUtil.isNotEmpty(map)).isTrue();

		//////////
		ConcurrentMap<String, Integer> map2 = MapUtil.newConcurrentHashMap();
		assertThat(MapUtil.putIfAbsentWithFinalValue(map2, "haha", 3)).isEqualTo(3);
		assertThat(MapUtil.putIfAbsentWithFinalValue(map2, "haha", 4)).isEqualTo(3);

		MapUtil.createIfAbsent(map2, "haha", new ValueCreator<Integer>() {
			@Override
			public Integer get() {
				return 5;
			}
		});

		assertThat(map2).hasSize(1).containsEntry("haha", 3);

		MapUtil.createIfAbsent(map2, "haha2", new ValueCreator<Integer>() {
			@Override
			public Integer get() {
				return 5;
			}
		});

		assertThat(map2).hasSize(2).containsEntry("haha2", 5);

	}

	@Test
	public void guavaBuildMap() {
		HashMap<String, Integer> map1 = MapUtil.newHashMap();

		HashMap<String, Integer> map2 = MapUtil.newHashMapWithCapacity(10, 0.5f);
		map2 = MapUtil.newHashMapWithCapacity(10, 0.5f);

		HashMap<String, Integer> map3 = MapUtil.newHashMap("1", 1);
		assertThat(map3).hasSize(1).containsEntry("1", 1);

		HashMap<String, Integer> map4 = MapUtil.newHashMap(new String[] { "1", "2" }, new Integer[] { 1, 2 });
		assertThat(map4).hasSize(2).containsEntry("1", 1).containsEntry("2", 2);

		HashMap<String, Integer> map5 = MapUtil.newHashMap(ArrayUtil.asList("1", "2", "3"), ArrayUtil.asList(1, 2, 3));
		assertThat(map5).hasSize(3).containsEntry("1", 1).containsEntry("2", 2).containsEntry("3", 3);

		TreeMap<String, Integer> map6 = MapUtil.newSortedMap();

		TreeMap<String, Integer> map7 = MapUtil.newSortedMap(Ordering.natural());

		ConcurrentMap map8 = MapUtil.newConcurrentHashMap();
		ConcurrentSkipListMap map9 = MapUtil.newConcurrentSortedMap();

		EnumMap map10 = MapUtil.newEnumMap(EnumA.class);
	}

	@Test
	public void jdkBuildMap() {
		Map<String, Integer> map1 = MapUtil.emptyMap();
		assertThat(map1).hasSize(0);

		Map<String, Integer> map2 = MapUtil.emptyMapIfNull(null);
		assertThat(map2).isNotNull().hasSize(0);

		Map<String, Integer> map3 = MapUtil.emptyMapIfNull(map1);
		assertThat(map3).isSameAs(map1);

		Map<String, Integer> map4 = MapUtil.singletonMap("haha", 1);
		assertThat(map4).hasSize(1).containsEntry("haha", 1);
		try {
			map4.put("dada", 2);
			fail("should fail before");
		} catch (Throwable t) {
			assertThat(t).isInstanceOf(UnsupportedOperationException.class);
		}

		Map<String, Integer> map5 = MapUtil.newHashMap();
		Map<String, Integer> map6 = MapUtil.unmodifiableMap(map5);

		try {
			map6.put("a", 2);
			fail("should fail before");
		} catch (Throwable t) {
			assertThat(t).isInstanceOf(UnsupportedOperationException.class);
		}

	}

	public enum EnumA {
		A, B, C
	}
}
