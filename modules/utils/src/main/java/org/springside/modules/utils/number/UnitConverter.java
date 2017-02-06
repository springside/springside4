/*
 * Copyright (C) 2012 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springside.modules.utils.number;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 将带单位的时间，大小字符串转换为数字.
 * 
 * from Facebook
 * https://github.com/facebook/jcommon/blob/master/config/src/main/java/com/facebook/config/ConfigUtil.java
 * 
 * @author calvin
 *
 */
public class UnitConverter {

	private static final Pattern NUMBER_AND_UNIT = Pattern.compile("(\\d+)([a-zA-Z]+)?");

	/**
	 * 将带单位的时间字符串转化为毫秒数.
	 * 
	 * 单位包括不分大小写的ms(毫秒),s(秒),m(分钟),h(小时),d(日),y(年)
	 * 
	 * 不带任何单位的话，默认单位是毫秒
	 */
	public static long convertDurationMillis(String duration) {
		Matcher matcher = NUMBER_AND_UNIT.matcher(duration);

		if (matcher.matches()) {
			long number = Long.parseLong(matcher.group(1));

			if (matcher.group(2) != null) {
				String unitStr = matcher.group(2).toLowerCase();
				char unit = unitStr.charAt(0);

				switch (unit) {
				case 's':
					return number * 1000;
				case 'm':
					// if it's an m, could be 'minutes' or 'millis'. default minutes
					if (unitStr.length() >= 2 && unitStr.charAt(1) == 's') {
						return number;
					}

					return number * 60 * 1000;
				case 'h':
					return number * 60 * 60 * 1000;
				case 'd':
					return number * 60 * 60 * 24 * 1000;
				default:
					throw new IllegalArgumentException("unknown time unit :" + unit);
				}
			} else {
				return number;
			}
		} else {
			throw new IllegalArgumentException("malformed duration string: " + duration);
		}
	}

	/**
	 * 将带单位的大小字符串转化为字节数.
	 * 
	 * 单位包括不分大小写的b(b),k(kb),m(mb),g(gb),t(tb)
	 * 
	 * 不带任何单位的话，默认单位是b
	 */
	public static long convertSizeBytes(String size) {
		Matcher matcher = NUMBER_AND_UNIT.matcher(size);

		if (matcher.matches()) {
			long number = Long.parseLong(matcher.group(1));

			if (matcher.group(2) != null) {
				char unit = matcher.group(2).toLowerCase().charAt(0);

				switch (unit) {
				case 'b':
					return number;
				case 'k':
					return number * 1024;
				case 'm':
					return number * 1024 * 1024;
				case 'g':
					return number * 1024 * 1024 * 1024;
				case 't':
					return number * 1024 * 1024 * 1024 * 1024;
				default:
					throw new IllegalArgumentException("unknown size unit :" + unit);
				}
			} else {
				return number;
			}
		} else {
			throw new IllegalArgumentException("malformed size string: " + size);
		}
	}
}
