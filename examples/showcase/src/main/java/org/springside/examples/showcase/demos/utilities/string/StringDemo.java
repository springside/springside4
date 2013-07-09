package org.springside.examples.showcase.demos.utilities.string;

import static org.junit.Assert.*;

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
		assertFalse(StringUtils.isNotBlank(null));
		assertFalse(StringUtils.isNotBlank(""));
		assertFalse(StringUtils.isNotBlank(" "));

		// null的default值
		assertEquals("", StringUtils.defaultString(null));
		assertEquals("defaultStr", StringUtils.defaultString(null, "defaultStr"));
		assertEquals("defaultStr", StringUtils.defaultIfBlank(null, "defaultStr"));
		assertEquals("defaultStr", StringUtils.defaultIfBlank(" ", "defaultStr"));

		// 截取字符串
		String input = "hahakaka";
		String result = StringUtils.substringAfter(input, "ha");
		assertEquals("hakaka", result);

		result = StringUtils.substringAfterLast(input, "ha");
		assertEquals("kaka", result);

		assertEquals("haha", StringUtils.substringBetween("'haha'", "'"));
		assertEquals("haha", StringUtils.substringBetween("{haha}", "{", "}"));

		// join
		List<String> inputList = Lists.newArrayList("a", "b", "c");
		result = StringUtils.join(inputList, ",");
		assertEquals("a,b,c", result);

		// ignoreCase的比较函数:contains/startWith/EndWith/indexOf/lastIndexOf
		assertTrue(StringUtils.containsIgnoreCase("Aaabbb", "aaa"));
		assertEquals(0, StringUtils.indexOfIgnoreCase("Aaabbb", "aaa"));

		// /split
		input = "a,b,c";
		String[] resultArray = StringUtils.split(input, ",");
		assertEquals("b", resultArray[1]);

		// 左边补0
		result = StringUtils.leftPad("1", 3, '0');
		assertEquals("001", result);

		// 超长部分变省略号
		assertEquals("abcdefg", StringUtils.abbreviate("abcdefg", 7));
		assertEquals("abc...", StringUtils.abbreviate("abcdefg", 6));

		// 首字母大写
		assertEquals("Abc", StringUtils.capitalize("abc"));
		assertEquals("abc", StringUtils.uncapitalize("Abc"));
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
		assertEquals("Space Opera, Horror, Magic realism, Religion", joined);

		// 将Null值转换为特定字符串.
		String[] fantasyGenres2 = { "Space Opera", null, "Horror", "Magic realism", null, "Religion" };
		joined = Joiner.on(", ").useForNull("NULL!!!").join(fantasyGenres2);
		assertEquals("Space Opera, NULL!!!, Horror, Magic realism, NULL!!!, Religion", joined);

		// join Map类型
		Map<Integer, String> map = Maps.newHashMap();
		map.put(1, "Space Opera");
		map.put(2, "Horror");
		map.put(3, "Magic realism");
		joined = Joiner.on(",").withKeyValueSeparator(":").join(map);
		assertEquals("1:Space Opera,2:Horror,3:Magic realism", joined);

		// append一个已存在的字符串
		StringBuilder sb = new StringBuilder("Append StringBulder demo: ");
		joined = Joiner.on(", ").skipNulls().appendTo(sb, fantasyGenres).toString();
		assertEquals("Append StringBulder demo: Space Opera, Horror, Magic realism, Religion", joined);
	}

	/**
	 * 好玩的CaseFormat转换，在spaceSize->space_size之间转换，比如数据库表名与Java类名，变量名之间的转换。
	 */
	@Test
	public void caseFormatByGuava() {
		String name = "SPACE_SIZE";
		assertEquals("SpaceSize", CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name));
		assertEquals("spaceSize", CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name));
		assertEquals("space_size", CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, name));
		assertEquals("space-size", CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name));
	}
}
