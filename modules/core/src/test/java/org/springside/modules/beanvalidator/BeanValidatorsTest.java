package org.springside.modules.beanvalidator;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

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
		// To avoid the non-English environment test failure on message asserts.
		Locale.setDefault(Locale.ENGLISH);
	}

	@Test
	public void validate() {

		Customer customer = new Customer();
		customer.setEmail("aaa");

		Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
		assertEquals(2, violations.size());

		// extract message as list
		List<String> result = BeanValidators.extractMessage(violations);
		assertTrue(result.contains("not a well-formed email address"));
		assertTrue(result.contains("may not be empty"));

		// extract propertyPath and message as map;
		Map mapResult = BeanValidators.extractPropertyAndMessage(violations);
		assertEquals("not a well-formed email address", mapResult.get("email"));
		assertEquals("may not be empty", mapResult.get("name"));

		// extract propertyPath and message as map;
		result = BeanValidators.extractPropertyAndMessageAsList(violations);
		assertTrue(result.contains("email not a well-formed email address"));
		assertTrue(result.contains("name may not be empty"));
	}

	@Test
	public void validateWithException() {
		Customer customer = new Customer();
		customer.setEmail("aaa");

		try {
			BeanValidators.validateWithException(validator, customer);
			Assert.fail("should throw excepion");
		} catch (ConstraintViolationException e) {
			Map mapResult = BeanValidators.extractPropertyAndMessage(e);
			assertEquals("not a well-formed email address", mapResult.get("email"));
			assertEquals("may not be empty", mapResult.get("name"));
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
