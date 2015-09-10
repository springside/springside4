/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.demos.utilities.string;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * 演示Apache Commons Lang3的StringUtils。
 * 
 * Apache的StringUtils已提供绝大多数String函数的Null Safe版本, 没有一一演示.
 * 
 * @author calvin
 */
public class ApacheStringUtilsDemo {

	@Test
	public void nullSafe() {
		// 判断非空，最常用函数
		assertThat(StringUtils.isNotBlank(null)).isFalse();
		assertThat(StringUtils.isNotBlank("")).isFalse();
		assertThat(StringUtils.isNotBlank("  ")).isFalse();

		assertThat(StringUtils.isNotEmpty("  ")).isTrue();

		// 对null或blank字符串的default值
		assertThat(StringUtils.defaultString(null)).isEqualTo("");
		assertThat(StringUtils.defaultString(null, "defaultStr")).isEqualTo("defaultStr");

		assertThat(StringUtils.defaultIfBlank(null, "defaultStr")).isEqualTo("defaultStr");
		assertThat(StringUtils.defaultIfBlank(" ", "defaultStr")).isEqualTo("defaultStr");
	}

	@Test
	public void substring() {
		String input = "hahakaka";
		String result = StringUtils.substringAfter(input, "ha");
		assertThat(result).isEqualTo("hakaka");

		result = StringUtils.substringAfterLast(input, "ha");
		assertThat(result).isEqualTo("kaka");

		assertThat(StringUtils.substringBetween("'haha'", "'")).isEqualTo("haha");
		assertThat(StringUtils.substringBetween("{haha}", "{", "}")).isEqualTo("haha");
	}

	@Test
	public void joinSplit() {
		// join
		List<String> inputList = Lists.newArrayList("a", "b", "c");
		String result = StringUtils.join(inputList, ",");
		assertThat(result).isEqualTo("a,b,c");

		// split
		String input = "a,b,c";
		String[] resultArray = StringUtils.split(input, ",");
		assertThat(resultArray).containsSequence("a", "b", "c");
	}

	@Test
	public void otherUtils() {
		// ignoreCase的比较函数:contains/startWith/EndWith/indexOf/lastIndexOf
		assertThat(StringUtils.containsIgnoreCase("Aaabbb", "aaa")).isTrue();
		assertThat(StringUtils.indexOfIgnoreCase("Aaabbb", "aaa")).isEqualTo(0);

		// 左边补0
		assertThat(StringUtils.leftPad("1", 3, '0')).isEqualTo("001");
		assertThat(StringUtils.leftPad("12", 3, '0')).isEqualTo("012");

		// 超长部分变省略号
		assertThat(StringUtils.abbreviate("abcdefg", 7)).isEqualTo("abcdefg");
		assertThat(StringUtils.abbreviate("abcdefg", 6)).isEqualTo("abc...");

		// 首字母大写/小写
		assertThat(StringUtils.capitalize("abc")).isEqualTo("Abc");
		assertThat(StringUtils.uncapitalize("Abc")).isEqualTo("abc");
	}
}
