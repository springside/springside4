package org.springside.examples.showcase.utilities.collection;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springside.examples.showcase.common.entity.User;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SimpleWayDemo {

	@Test
	public void simple() {
		//无需在等号右边重新定义泛型的创建ArrayList
		List<String> list = Lists.newArrayList();
		//创建的同时初始化数据
		List<String> list2 = Lists.newArrayList("a", "b", "c");
		//无需在等号右边重新定义泛型的创建HashMap
		Map<String, ? extends User> map = Maps.newHashMap();
	}
}
