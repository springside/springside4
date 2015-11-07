/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.springside.modules.persistence.SearchFilter.Operator;

import com.google.common.collect.Maps;

public class BetweenSearchFilterTest {

    @Test
    public void normal() throws ParseException {
        // linkedHashMap保证顺序
        Map<String, Object> params = Maps.newLinkedHashMap();
        params.put("BTW_age_start", 18);
        params.put("BTW_age_end", 36);

        Date start = DateUtils.parseDate("20150926", "yyyyMMdd");
        Date end = DateUtils.parseDate("20151001", "yyyyMMdd");
        params.put("BTW_sendDate_start", start);
        params.put("BTW_sendDate_end", end);

        Map<String, SearchFilter> filters = SearchFilter.parse(params);

        BetweenSearchFilter ageFilter = (BetweenSearchFilter) filters.get("BTW_age");
        assertThat(ageFilter.operator).isEqualTo(Operator.BTW);
        assertThat(ageFilter.fieldName).isEqualTo("age");
        assertThat(ageFilter.start).isEqualTo(18);
        assertThat(ageFilter.end).isEqualTo(36);

        BetweenSearchFilter sendDateFilter = (BetweenSearchFilter) filters.get("BTW_sendDate");
        assertThat(sendDateFilter.operator).isEqualTo(Operator.BTW);
        assertThat(sendDateFilter.fieldName).isEqualTo("sendDate");
        assertThat(sendDateFilter.start).isEqualTo(start);
        assertThat(sendDateFilter.end).isEqualTo(end);
    }

    @Test
    public void wrongName() {
        try {
            Map<String, Object> params = Maps.newLinkedHashMap();
            params.put("BTW", "foo");

            SearchFilter.parse(params);
            fail("should fail with wrong name");
        } catch (IllegalArgumentException e) {
        }

        try {
            Map<String, Object> params = Maps.newLinkedHashMap();
            params.put("BTW_", "foo");

            SearchFilter.parse(params);
            fail("should fail with wrong name");
        } catch (IllegalArgumentException e) {
        }

        try {
            Map<String, Object> params = Maps.newLinkedHashMap();
            params.put("ABC_name", "foo");

            SearchFilter.parse(params);
            fail("should fail with wrong operator name");
        } catch (IllegalArgumentException e) {
        }
    }
}
