package org.springside.modules.persistence;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

public class SearchFilter {

	public enum Operator {
		EQ, LIKE, GT, LT, GTE, LTE, IN;
	}

	public String fieldName;
	public Object value;
	public Operator operator;

	public SearchFilter(String fieldName, Operator operator, Object value) {
		this.fieldName = fieldName;
		this.value = value;
		this.operator = operator;
	}

	/**
	 * searchParams中key的格式为OPERATOR_FIELDNAME
	 */
	public static Map<String, SearchFilter> parse(Map<String, Object> searchParams) {
		Map<String, SearchFilter> filters = Maps.newHashMap();

		for (Entry<String, Object> entry : searchParams.entrySet()) {
			// 过滤掉空值
			String key = entry.getKey();
			Object value = entry.getValue();

			if ((value == null)) {
				// value wont be null here indeed
				continue;
			}

			boolean isValidString = (value instanceof String) && StringUtils.isNotBlank((String) value);
			boolean isValidList = (value instanceof String[]) && (((String[]) value).length > 0);
			if (isValidString || isValidList) {
				String[] names = StringUtils.split(key, "_");
				if (names.length != 2) {
					throw new IllegalArgumentException(key + " is not a valid search filter name");
				}
				String filedName = names[1];
				Operator operator = Operator.valueOf(names[0].toUpperCase());
				SearchFilter filter = new SearchFilter(filedName, operator, value);
				filters.put(key, filter);
			}
		}

		return filters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SearchFilter [fieldName=" + fieldName + ", value=" + value + ", operator=" + operator + "]";
	}

}
