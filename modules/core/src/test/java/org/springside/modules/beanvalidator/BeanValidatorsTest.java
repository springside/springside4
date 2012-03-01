package org.springside.modules.beanvalidator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.beanvalidator.BeanValidators;
import org.springside.modules.test.spring.SpringContextTestCase;

@ContextConfiguration(locations = { "/applicationContext-core-test.xml" })
public class BeanValidatorsTest extends SpringContextTestCase {

	@Autowired
	Validator validator;

	@Test
	public void validate() {
		Customer customer = new Customer();
		customer.setEmail("aaa");

		Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
		assertEquals(2, violations.size());
		String result = BeanValidators.convertMessage(violations, ",");
		assertTrue(StringUtils.contains(result, "邮件地址格式不正确"));
		assertTrue(StringUtils.contains(result, "姓名不能为空"));
	}

	@Test
	public void validateWithException() {
		Customer customer = new Customer();
		customer.setEmail("aaa");

		try {
			BeanValidators.validateWithException(validator, customer);
			Assert.fail("should throw excepion");
		} catch (ConstraintViolationException e) {
			String result = BeanValidators.convertMessage(e, ",");
			assertTrue(StringUtils.contains(result, "邮件地址格式不正确"));
			assertTrue(StringUtils.contains(result, "姓名不能为空"));
		}

	}

	private static class Customer {

		String name;

		String email;

		@NotBlank(message = "姓名不能为空")
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Email(message = "邮件地址格式不正确")
		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

	}
}
