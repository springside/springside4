package org.springside.examples.showcase.demos.utilities.collection;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springside.examples.showcase.entity.User;
import org.springside.modules.utils.Collections3;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 演示各种Collections如何简化Collection操作.
 * 
 * @author calvin
 */
public class CollectionsDemo {

	@Test
	public void init() {
		// 无需在等号右边重新定义泛型的创建ArrayList
		List<String> list = Lists.newArrayList();
		// 创建的同时初始化数据
		List<String> list2 = Lists.newArrayList("a", "b", "c");

		// 无需在等号右边重新定义泛型的创建HashMap
		Map<String, ? extends User> map = Maps.newHashMap();
		// 创建Map的同时初始化值，不过这个map是不可修改的，主要用于编写测试用例。
		Map<Integer, String> unmodifiedMap = ImmutableMap.of(1, "foo", 2, "bar");
	}

	@Test
	public void operation() {
		List<String> list = Lists.newArrayList("a", "b", "c");
		List<String> list2 = Lists.newArrayList("a", "b");

		// nullsafe的判断是否为空
		assertFalse(Collections3.isEmpty(list));

		// 获取最后一个
		assertEquals("c", Collections3.getLast(list));

		// list+list2的新List
		List result = Collections3.union(list, list2);
		assertEquals("[a, b, c, a, b]", result.toString());

		// list-list2的新List
		result = Collections3.subtract(list, list2);
		assertEquals("[c]", result.toString());

		// list与list2的交集的新List
		result = Collections3.intersection(list, list2);
		assertEquals("[a, b]", result.toString());

	}
}
