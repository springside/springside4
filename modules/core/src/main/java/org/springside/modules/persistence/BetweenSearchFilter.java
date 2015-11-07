/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2015 All Rights Reserved.
 */
package org.springside.modules.persistence;

/**
 * 基于between两个参数实现的searchFilter
 * @author Fei
 * @version $Id: BetweenSearchFilter.java, v 0.1 2015年9月26日 下午8:22:32 Fei Exp $
 */
public class BetweenSearchFilter extends SearchFilter {

    /** 开始 */
    public Object start;
    /** 结束 */
    public Object end;

    /**
     * @param fieldName
     * @param operator
     * @param value
     * @param startDate
     * @param endDate
     */
    public BetweenSearchFilter(String fieldName, Object start, Object end) {
        super(fieldName, Operator.BTW, null);
        this.start = start;
        this.end = end;
    }

    /**
     * @param fieldName
     * @param operator
     * @param value
     */
    public BetweenSearchFilter(String fieldName, Operator operator, Object value) {
        super(fieldName, operator, value);
    }

}
