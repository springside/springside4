package org.springside.modules.beanvalidator;

import static org.junit.Assert.*;

import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.test.spring.SpringContextTestCase;

@ContextConfiguration(locations = { "/applicationContext-core-test.xml" })
public class BeanValidatorsTest extends SpringContextTestCase {

	@Autowired
	Validator validator;

	@BeforeClass
	public static void beforeClass() {
		//To avoid the non-English environment test failure on message asserts.
		Locale.setDefault(Locale.ENGLISH);
	}

	@Test
	public void validate() {

		Customer customer = new Customer();
		customer.setEmail("aaa");

		Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
		assertEquals(2, violations.size());
		String result = StringUtils.join(BeanValidators.extractPropertyAndMessage(violations, " "), ",");
		assertTrue(StringUtils.contains(result, "email not a well-formed email address"));
		assertTrue(StringUtils.contains(result, "name may not be empty"));

		result = StringUtils.join(BeanValidators.extractMessage(violations), ",");
		assertTrue(StringUtils.contains(result, "not a well-formed email address"));
		assertTrue(StringUtils.contains(result, "may not be empty"));
	}

	@Test
	public void validateWithException() {
		Customer customer = new Customer();
		customer.setEmail("aaa");

		try {
			BeanValidators.validateWithException(validator, customer);
			Assert.fail("should throw excepion");
		} catch (ConstraintViolationException e) {
			String result = StringUtils.join(BeanValidators.extractPropertyAndMessage(e), ",");
			assertTrue(StringUtils.contains(result, "email not a well-formed email address"));
			assertTrue(StringUtils.contains(result, "name may not be empty"));
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
