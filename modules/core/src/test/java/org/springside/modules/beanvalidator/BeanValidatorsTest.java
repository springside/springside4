/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.beanvalidator;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.spring.SpringContextTestCase;

@ContextConfiguration(locations = { "/applicationContext-core-test.xml" })
public class BeanValidatorsTest extends SpringContextTestCase {

	@Autowired
	private Validator validator;

	@BeforeClass
	public static void beforeClass() {
		// To avoid the non-English environment test failure on message asserts.
		Locale.setDefault(Locale.ENGLISH);
	}

	@Test
	public void validate() {

		Customer customer = new Customer();
		customer.setEmail("aaa");

		Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
		assertThat(violations).hasSize(2);

		// extract message as list
		List<String> result = BeanValidators.extractMessage(violations);
		assertThat(result).containsOnly("not a well-formed email address", "may not be empty");

		// extract propertyPath and message as map;
		Map mapResult = BeanValidators.extractPropertyAndMessage(violations);
		assertThat(mapResult).containsOnly(entry("email", "not a well-formed email address"),
				entry("name", "may not be empty"));

		// extract propertyPath and message as map;
		result = BeanValidators.extractPropertyAndMessageAsList(violations);
		assertThat(result).containsOnly("email not a well-formed email address", "name may not be empty");
	}

	@Test
	public void validateWithException() {
		Customer customer = new Customer();
		customer.setEmail("aaa");

		try {
			BeanValidators.validateWithException(validator, customer);
			failBecauseExceptionWasNotThrown(ConstraintViolationException.class);
		} catch (ConstraintViolationException e) {
			Map mapResult = BeanValidators.extractPropertyAndMessage(e);
			assertThat(mapResult).contains(entry("email", "not a well-formed email address"),
					entry("name", "may not be empty"));
		}
	}

	private static class Customer {

		private String name;

		private String email;

		@NotBlank
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Email
		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

	}
}
