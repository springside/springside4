package org.springside.modules.utils.collection.type;

import static org.assertj.core.api.Assertions.*;

import java.util.TreeSet;

import org.junit.Test;
import org.springside.modules.utils.collection.SetUtil;

public class ComparableComparatorTest {

	@Test
	public void test() {
		TreeSet<String> set = SetUtil.newNavigableSet(ComparableComparator.INSTANCE);

		set.add("3");
		set.add("1");
		set.add("4");
		assertThat(set).containsSequence("1", "3", "4");
	}

}
