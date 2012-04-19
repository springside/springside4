package org.springside.examples.miniservice.service;

import static org.junit.Assert.*;

import java.util.Locale;

import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springside.examples.miniservice.data.AccountData;
import org.springside.examples.miniservice.entity.User;
import org.springside.modules.beanvalidator.BeanValidators;
import org.springside.modules.test.spring.SpringContextTestCase;

@ContextConfiguration(locations = { "/applicationContext.xml" })
public class AccountManagerTest extends SpringContextTestCase {

	@Autowired
	private AccountManager accountManager;

	@BeforeClass
	public static void beforeClass() {
		//To avoid the non-English environment test failure on message asserts.
		Locale.setDefault(Locale.ENGLISH);
	}

	/**
	 * 测试参数校验.
	 */
	@Test
	public void validateParamter() {

		User userDTOWithoutLoginName = AccountData.getRandomUser();
		userDTOWithoutLoginName.setLoginName(null);
		try {
			accountManager.saveUser(userDTOWithoutLoginName);
			fail("exception should be thrown");
		} catch (ConstraintViolationException e) {
			assertEquals("loginName may not be empty",
					StringUtils.join(BeanValidators.extractPropertyAndMessage(e), ','));
		}

		User userDTOWitWrongEmail = AccountData.getRandomUser();
		userDTOWitWrongEmail.setEmail("abc");
		try {
			accountManager.saveUser(userDTOWitWrongEmail);
			fail("exception should be thrown");
		} catch (ConstraintViolationException e) {
			assertEquals("email not a well-formed email address",
					StringUtils.join(BeanValidators.extractPropertyAndMessage(e), ","));
		}
	}
}
