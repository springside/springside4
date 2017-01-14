package org.springside.modules.utils.collection;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Set;

import org.junit.Test;

public class CollectionUtilTest {

	@Test
	public void test() {
		List<String> list1 = ListUtil.newArrayList();

		List<String> list2 = ListUtil.newArrayList("a", "b", "c");
		List<String> list3 = ListUtil.newArrayList("a");

		Set<String> set1 = SetUtil.newSortedSet();
		set1.add("a");
		set1.add("b");
		set1.add("c");

		Set<String> set2 = SetUtil.newSortedSet();
		set2.add("a");

		assertThat(CollectionUtil.isEmpty(list1)).isTrue();
		assertThat(CollectionUtil.isEmpty(null)).isTrue();
		assertThat(CollectionUtil.isEmpty(list2)).isFalse();

		assertThat(CollectionUtil.isNotEmpty(list1)).isFalse();
		assertThat(CollectionUtil.isNotEmpty(null)).isFalse();
		assertThat(CollectionUtil.isNotEmpty(list2)).isTrue();

		assertThat(CollectionUtil.getFirst(list2)).isEqualTo("a");
		assertThat(CollectionUtil.getLast(list2)).isEqualTo("c");

		assertThat(CollectionUtil.getFirst(set1)).isEqualTo("a");
		assertThat(CollectionUtil.getLast(set1)).isEqualTo("c");

		assertThat(CollectionUtil.getFirst(list3)).isEqualTo("a");
		assertThat(CollectionUtil.getLast(list3)).isEqualTo("a");

		assertThat(CollectionUtil.getFirst(set2)).isEqualTo("a");
		assertThat(CollectionUtil.getLast(set2)).isEqualTo("a");

		assertThat(CollectionUtil.getFirst(list1)).isNull();
		assertThat(CollectionUtil.getFirst(null)).isNull();
		assertThat(CollectionUtil.getLast(list1)).isNull();
		assertThat(CollectionUtil.getLast(null)).isNull();
	}

}
