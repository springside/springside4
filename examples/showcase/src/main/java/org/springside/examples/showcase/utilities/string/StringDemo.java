package org.springside.examples.showcase.utilities.string;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class StringDemo {

	@Test
	public void utilsByApache() {
		//非空，最常用函数
		assertFalse(StringUtils.isNotBlank(null));
		assertFalse(StringUtils.isNotBlank(""));
		assertFalse(StringUtils.isNotBlank(" "));

		//截取字符串
		String input = "hahakaka";
		String result = StringUtils.substringAfter(input, "ha");
		assertEquals("hakaka", result);

		result = StringUtils.substringAfterLast(input, "ha");
		assertEquals("kaka", result);

		//join
		List<String> inputList = Lists.newArrayList("a", "b", "c");
		result = StringUtils.join(inputList, ",");
		assertEquals("a,b,c", result);

		///split
		input = "a,b,c";
		String[] resultArray = StringUtils.split(input, ",");
		assertEquals("b", resultArray[1]);

		//左边补0
		result = StringUtils.leftPad("1", 3, '0');
		assertEquals("001", result);

		//首字母大写
		result = StringUtils.capitalize("abc");
		assertEquals("Abc", result);
	}

	@Test
	public void joinByGuava() {

		//忽略Null值。
		String[] fantasyGenres = { "Space Opera", null, "Horror", "Magic realism", null, "Religion" };
		String joined = Joiner.on(", ").skipNulls().join(fantasyGenres);
		System.out.println(joined);

		//将Null值转换为特定字符串.
		String[] fantasyGenres2 = { "Space Opera", null, "Horror", "Magic realism", null, "Religion" };
		joined = Joiner.on(", ").useForNull("NULL!!!").join(fantasyGenres2);
		System.out.println(joined);

		//join Map类型
		Map<Integer, String> map = Maps.newHashMap();
		map.put(1, "Space Opera");
		map.put(2, "Horror");
		map.put(3, "Magic realism");
		joined = Joiner.on(",").withKeyValueSeparator(":").join(map);
		System.out.println(joined);

		//append一个已存在的字符串
		StringBuilder sb = new StringBuilder("StringBulder Demo: ");
		joined = Joiner.on(", ").skipNulls().appendTo(sb, fantasyGenres).toString();
		System.out.println(joined);
	}

	@Test
	public void caseFormatByGuava() {
		String name = "SPACE_SIZE";
		assertEquals("SpaceSize", CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name));
		assertEquals("spaceSize", CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name));
		assertEquals("space_size", CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, name));
		assertEquals("space-size", CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name));
	}
}
