package org.springside.modules.utils.text;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Lists;

public class Strings {

	/**
	 * 高性能的Split，针对char的分隔符号，比JDK String自带的高效.
	 * 
	 * from Commons Lange 3.5 StringUtils, 做如下优化:
	 * 
	 * 1. 最后不做数组转换，直接返回List.
	 * 
	 * 2. 可设定List初始大小.
	 */
	public static List<String> split(final String str, final char separatorChar, int expectParts) {
		if (str == null) {
			return null;
		}
		final int len = str.length();
		if (len == 0) {
			return Lists.emptyList();
		}
		final List<String> list = new ArrayList<String>(expectParts);
		int i = 0, start = 0;
		boolean lastMatch = false;
		while (i < len) {
			if (str.charAt(i) == separatorChar) {

				list.add(str.substring(start, i));
				lastMatch = true;
				start = ++i;
				continue;
			}
			lastMatch = false;
			i++;
		}
		if (lastMatch) {
			list.add(str.substring(start, i));
		}
		return list;
	}

}
