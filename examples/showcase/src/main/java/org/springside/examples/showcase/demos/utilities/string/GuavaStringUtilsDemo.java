/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.demos.utilities.string;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.base.CaseFormat;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

/**
 * 演示 Guava的比Apache StringUtils更高级的Joiner和Spliter，Case Format转换
 * 
 * @author calvin
 */
public class GuavaStringUtilsDemo {

	/**
	 * Guava的高级版Joiner，
	 */
	@Test
	public void joiner() {

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

		// reuse joiner
		Joiner joiner = Joiner.on(", ");
		joiner.join(fantasyGenres);
		joiner.join(fantasyGenres2);

	}

	/**
	 * Splitter有很多函数与Joiner一样，不一一演示
	 */
	@Test
	public void splitter() {
		// 去除逗号前后的空格
		String input = "Space Opera,Horror, Magic realism,Religion";
		List<String> result = Splitter.on(",").trimResults().splitToList(input);
		assertThat(result).containsSequence("Space Opera", "Horror", "Magic realism", "Religion");
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

	@Test
	public void otherUtils() {
		// 直接getBytes, 无需catch UnsupportedEncodingException, JDK7 有相应的StandardCharsets
		byte[] bytes = "foobarbaz".getBytes(Charsets.UTF_8);
	}
}
