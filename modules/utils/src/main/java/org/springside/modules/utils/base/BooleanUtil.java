package org.springside.modules.utils.base;

public class BooleanUtil {

	public static Boolean toBooleanObject(String str, Boolean defaultValue) {
		if (str == null) {
			return defaultValue;
		} else {
			return Boolean.valueOf(str);
		}
	}

}
