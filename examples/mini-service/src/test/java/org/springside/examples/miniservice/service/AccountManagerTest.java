package org.springside.examples.miniservice.service;

import static org.junit.Assert.*;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springside.examples.miniservice.data.AccountData;
import org.springside.examples.miniservice.entity.User;
import org.springside.modules.test.spring.SpringContextTestCase;
import org.springside.modules.utils.ee.Validators;

@ContextConfiguration(locations = { "/applicationContext.xml" })
public class AccountManagerTest extends SpringContextTestCase {

	@Autowired
	private AccountManager accountManager;

	/**
	 * 测试参数校验.
	 */
	@Test
	public void validateParamter() {

		User userDTOWithoutLoginName = AccountData.getRandomUser();
		userDTOWithoutLoginName.setLoginName(null);
		try {
			accountManager.saveUser(userDTOWithoutLoginName);
		} catch (ConstraintViolationException e) {
			assertEquals("登录名不能为空", Validators.convertMessage(e, ","));
		}
		User userDTOWitWrongEmail = AccountData.getRandomUser();
		userDTOWitWrongEmail.setEmail("abc");
		try {
			accountManager.saveUser(userDTOWitWrongEmail);
		} catch (ConstraintViolationException e) {
			assertEquals("邮件地址格式不正确", Validators.convertMessage(e, ","));
		}

	}
}
