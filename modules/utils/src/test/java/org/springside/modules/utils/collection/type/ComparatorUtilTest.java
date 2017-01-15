package org.springside.modules.utils.collection.type;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.TreeSet;

import org.junit.Test;
import org.springside.modules.utils.collection.ComparatorUtil;
import org.springside.modules.utils.collection.ListUtil;
import org.springside.modules.utils.collection.SetUtil;

public class ComparatorUtilTest {

	@Test
	public void test() {
		TreeSet<String> set = SetUtil.newSortedSet(ComparatorUtil.NATUAL);

		set.add("3");
		set.add("1");
		set.add("4");
		assertThat(set).containsExactly("1", "3", "4");

		TreeSet<String> set2 = SetUtil.newSortedSet(ComparatorUtil.reverseNatural());

		set2.add("3");
		set2.add("1");
		set2.add("4");

		assertThat(set2).containsExactly("4", "3", "1");

		List<String> list = ListUtil.newArrayList("3", null, "4", "1");

		ListUtil.sort(list, ComparatorUtil.nullFirstNatural());
		assertThat(list).containsExactly(null, "1", "3", "4");

		ListUtil.sort(list, ComparatorUtil.nullLastNatural());
		assertThat(list).containsExactly("1", "3", "4", null);

		ListUtil.sort(list, ComparatorUtil.reverse(ComparatorUtil.nullLastNatural()));
		assertThat(list).containsExactly(null, "4", "3", "1");
		
		ListUtil.sort(list, ComparatorUtil.nullFirst(ComparatorUtil.natural()));
		assertThat(list).containsExactly(null, "1", "3", "4");
		
		ListUtil.sort(list, ComparatorUtil.nullLast(ComparatorUtil.natural()));
		assertThat(list).containsExactly("1", "3", "4", null);

	}

}
