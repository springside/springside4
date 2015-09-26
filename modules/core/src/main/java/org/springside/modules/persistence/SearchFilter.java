/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.persistence;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

public class SearchFilter {

    public enum Operator {
        EQ, LIKE, GT, LT, GTE, LTE, BTW
    }

    public String   fieldName;
    public Object   value;
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
            if (value == null
                || ((value instanceof String) && (StringUtils.isBlank((String) value)))) {
                continue;
            }

            // 拆分operator与filedAttribute
            String[] names = StringUtils.split(key, "_");
            if (names.length != 2 && names.length != 3) {
                throw new IllegalArgumentException(key + " is not a valid search filter name");
            }
            String filedName = names[1];
            Operator operator = Operator.valueOf(names[0]);

            // 创建searchFilter
            SearchFilter filter = null;

            if (operator == Operator.BTW) {
                //针对between filter做处理,key做特殊处理，修改为标准模式 operator_字段名
                key = names[0] + "_" + names[1];
                filter = parseBetweenFilter(key, names, filedName, value, filters);
            } else {
                filter = new SearchFilter(filedName, operator, value);
            }

            filters.put(key, filter);
        }

        return filters;
    }

    /**
     * 针对betweenFilter的解析
     * @param key
     * @param names
     * @param filedName
     * @param value
     * @param filters
     * @return
     */
    public static SearchFilter parseBetweenFilter(String key, String[] names, String filedName,
                                                  Object value, Map<String, SearchFilter> filters) {

        SearchFilter filter = null;
        filter = filters.get(key);
        if (filter == null) {
            filter = new BetweenSearchFilter(filedName, Operator.BTW, null);
        }
        String index = names[2];
        if (StringUtils.equalsIgnoreCase("start", index)) {
            ((BetweenSearchFilter) filter).start = value;
        } else if (StringUtils.equalsIgnoreCase("end", index)) {
            ((BetweenSearchFilter) filter).end = value;
        } else {
            throw new IllegalArgumentException(key + " is not a valid search filter name");
        }
        return filter;
    }
}
