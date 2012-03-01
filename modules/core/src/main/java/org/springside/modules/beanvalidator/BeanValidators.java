/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.springside.modules.beanvalidator;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

/**
 * JSR303 Validator(Hibernate Validator)工具类.
 * 
 * @author badqiu
 * @author calvin
 */
public class BeanValidators {

	private BeanValidators() {
	}

	/**
	 * 调用JSR303的validate方法, 验证失败时抛出ConstraintViolationException.
	 */
	public static void validateWithException(Validator validator, Object object, Class<?>... groups)
			throws ConstraintViolationException {
		Set constraintViolations = validator.validate(object, groups);
		if (!constraintViolations.isEmpty()) {
			throw new ConstraintViolationException(constraintViolations);
		}
	}

	/**
	 * 辅助方法, 转换Set<ConstraintViolation>中所有的message为字符串, 以separator分隔.
	 */
	public static String convertMessage(Set<? extends ConstraintViolation> constraintViolations, String separator) {
		List<String> errorMessages = Lists.newArrayList();
		for (ConstraintViolation violation : constraintViolations) {
			errorMessages.add(violation.getMessage());
		}
		return StringUtils.join(errorMessages, separator);
	}

	/**
	 * 辅助方法, 转换ConstraintViolationException中的Set<ConstraintViolations>中的所有message为字符串, 以separator分隔.
	 */
	public static String convertMessage(ConstraintViolationException e, String separator) {
		return convertMessage(e.getConstraintViolations(), separator);
	}
}
