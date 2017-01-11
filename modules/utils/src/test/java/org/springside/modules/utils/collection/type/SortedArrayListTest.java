package org.springside.modules.utils.collection.type;

import static org.assertj.core.api.Assertions.*;

import org.junit.Assert;
import org.junit.Test;
import org.springside.modules.utils.collection.ListUtil;

public class SortedArrayListTest {

	@Test
	public void sortedArrayList() {
		SortedArrayList<String> list = ListUtil.newSortedArrayList();
		list.add("9");
		list.add("1");
		list.add("6");
		list.add("9");
		list.add("3");

		assertThat(list).containsSequence("1", "3", "6", "9", "9");

		list.remove(2);
		assertThat(list).containsSequence("1", "3", "9", "9");

		assertThat(list.contains("3")).isTrue();
		assertThat(list.contains("2")).isFalse();

		try {
			list.add(1, "2");
			Assert.fail("should fail before");
		} catch (Throwable t) {
			assertThat(t).isInstanceOf(UnsupportedOperationException.class);
		}
	}
}
