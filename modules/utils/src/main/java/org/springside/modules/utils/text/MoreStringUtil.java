package org.springside.modules.utils.text;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Lists;

import com.google.common.annotations.Beta;
import com.google.common.base.Utf8;

/**
 * 尽量使用Common Lang StringUtils, 完全覆盖了guva对应的部分(除了split和join的API没那么酷)
 * 
 * 本类仅补充少量额外方法.
 * 
 * @author calvin
 */
@Beta
public class MoreStringUtil {

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

	/**
	 * 计算字符串被UTF8编码后的字节数 via guava
	 * 
	 * @see Utf8#encodedLength(CharSequence)
	 */
	public static int utf8EncodedLength(CharSequence sequence) {
		return Utf8.encodedLength(sequence);
	}
}
