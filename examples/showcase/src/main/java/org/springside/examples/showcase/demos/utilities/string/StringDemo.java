/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.demos.utilities.string;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 演示Apache Commons Lang3的StringUtils 和 Guava的Joiner和Spliter。
 * 
 * Apache的StringUtils已提供绝大多数常用的Null Safe的函数，
 * Guava提供的是高级版的Joiner和Spliter,Case Format转换等高级函数.
 * 
 * @author calvin
 */
public class StringDemo {

	/**
	 * Apache提供的最常用的Null Safe的函数。
	 * 
	 * 另有一些与String一样，但Null Safe的函数，没有一一演示。
	 * 
	 */
	@Test
	public void utilsByApache() {
		// 判断非空，最常用函数
		assertThat(StringUtils.isNotBlank(null)).isFalse();
		assertThat(StringUtils.isNotBlank("")).isFalse();
		assertThat(StringUtils.isNotBlank(" ")).isFalse();

		// null的default值
		assertThat(StringUtils.defaultString(null)).isEqualTo("");
		assertThat(StringUtils.defaultString(null, "defaultStr")).isEqualTo("defaultStr");
		assertThat(StringUtils.defaultIfBlank(null, "defaultStr")).isEqualTo("defaultStr");
		assertThat(StringUtils.defaultIfBlank(" ", "defaultStr")).isEqualTo("defaultStr");

		// 截取字符串
		String input = "hahakaka";
		String result = StringUtils.substringAfter(input, "ha");
		assertThat(result).isEqualTo("hakaka");

		result = StringUtils.substringAfterLast(input, "ha");
		assertThat(result).isEqualTo("kaka");

		assertThat(StringUtils.substringBetween("'haha'", "'")).isEqualTo("haha");
		assertThat(StringUtils.substringBetween("{haha}", "{", "}")).isEqualTo("haha");

		// join
		List<String> inputList = Lists.newArrayList("a", "b", "c");
		result = StringUtils.join(inputList, ",");
		assertThat(result).isEqualTo("a,b,c");

		// ignoreCase的比较函数:contains/startWith/EndWith/indexOf/lastIndexOf
		assertThat(StringUtils.containsIgnoreCase("Aaabbb", "aaa")).isTrue();
		assertThat(StringUtils.indexOfIgnoreCase("Aaabbb", "aaa")).isEqualTo(0);

		// /split
		input = "a,b,c";
		String[] resultArray = StringUtils.split(input, ",");
		assertThat(resultArray[1]).isEqualTo("b");

		// 左边补0
		result = StringUtils.leftPad("1", 3, '0');
		assertThat(result).isEqualTo("001");

		// 超长部分变省略号
		assertThat(StringUtils.abbreviate("abcdefg", 7)).isEqualTo("abcdefg");
		assertThat(StringUtils.abbreviate("abcdefg", 6)).isEqualTo("abc...");

		// 首字母大写
		assertThat(StringUtils.capitalize("abc")).isEqualTo("Abc");
		assertThat(StringUtils.uncapitalize("Abc")).isEqualTo("abc");
	}

	/**
	 * Guava的高级版Joiner，
	 * 
	 * Splitter的函数与Joiner功能类似，不再演示。
	 */
	@Test
	public void joinerByGuava() {

		// 忽略Null值。
		String[] fantasyGenres = { "Space Opera", null, "Horror", "Magic realism", null, "Religion" };
		String joined = Joiner.on(", ").skipNulls().join(fantasyGenres);
		assertThat(joined).isEqualTo("Space Opera, Horror, Magic realism, Religion");

		// 将Null值转换为特定字符串.
		String[] fantasyGenres2 = { "Space Opera", null, "Horror", "Magic realism", null, "Religion" };
		joined = Joiner.on(", ").useForNull("NULL!!!").join(fantasyGenres2);
		assertThat(joined).isEqualTo("Space Opera, NULL!!!, Horror, Magic realism, NULL!!!, Religion");

		// join Map类型
		Map<Integer, String> map = Maps.newHashMap();
		map.put(1, "Space Opera");
		map.put(2, "Horror");
		map.put(3, "Magic realism");
		joined = Joiner.on(",").withKeyValueSeparator(":").join(map);
		assertThat(joined).isEqualTo("1:Space Opera,2:Horror,3:Magic realism");

		// append一个已存在的字符串
		StringBuilder sb = new StringBuilder("Append StringBulder demo: ");
		joined = Joiner.on(", ").skipNulls().appendTo(sb, fantasyGenres).toString();
		assertThat(joined).isEqualTo("Append StringBulder demo: Space Opera, Horror, Magic realism, Religion");
	}

	/**
	 * 好玩的CaseFormat转换，在spaceSize->space_size之间转换，比如数据库表名与Java类名，变量名之间的转换。
	 */
	@Test
	public void caseFormatByGuava() {
		String name = "SPACE_SIZE";
		assertThat(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name)).isEqualTo("SpaceSize");
		assertThat(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name)).isEqualTo("spaceSize");
		assertThat(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, name)).isEqualTo("space_size");
		assertThat(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name)).isEqualTo("space-size");
	}
}
