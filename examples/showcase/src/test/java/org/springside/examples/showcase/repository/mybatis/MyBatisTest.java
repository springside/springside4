package org.springside.examples.showcase.repository.mybatis;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springside.examples.showcase.entity.User;
import org.springside.modules.test.spring.SpringTransactionalTestCase;

import com.google.common.collect.Maps;

@ContextConfiguration(locations = { "/applicationContext.xml" })
@TransactionConfiguration(transactionManager = "defaultTransactionManager")
public class MyBatisTest extends SpringTransactionalTestCase {

	@Autowired
	private AccountDao accountMapper;

	@Test
	public void getUser() throws Exception {
		User user = accountMapper.getUser(1L);
		assertEquals("admin", user.getLoginName());
	}

	@Test
	public void searchUser() throws Exception {
		Map<String, Object> parameter = Maps.newHashMap();
		parameter.put("name", "Admin");
		List<User> result = accountMapper.searchUser(parameter);
		assertEquals(1, result.size());
		assertEquals("admin", result.get(0).getLoginName());
	}
}
