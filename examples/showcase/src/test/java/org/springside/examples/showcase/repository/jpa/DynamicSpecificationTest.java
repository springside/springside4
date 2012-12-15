package org.springside.examples.showcase.repository.jpa;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springside.examples.showcase.entity.User;
import org.springside.modules.persistence.DynamicSpecifications;
import org.springside.modules.persistence.SearchFilter;
import org.springside.modules.persistence.SearchFilter.Operator;
import org.springside.modules.test.spring.SpringTransactionalTestCase;

import com.google.common.collect.Lists;

@ContextConfiguration(locations = { "/applicationContext.xml" })
public class DynamicSpecificationTest extends SpringTransactionalTestCase {

	@Autowired
	private UserDao userDao;

	@Test
	public void fineUserByFilter() {
		// EQ
		SearchFilter filter = new SearchFilter("name", Operator.EQ, "管理员");
		List<User> users = userDao
				.findAll(DynamicSpecifications.bySearchFilter(Lists.newArrayList(filter), User.class));
		assertEquals(1, users.size());

		// LIKE
		filter = new SearchFilter("loginName", Operator.LIKE, "min");
		users = userDao.findAll(DynamicSpecifications.bySearchFilter(Lists.newArrayList(filter), User.class));
		assertEquals(1, users.size());

		// GT
		filter = new SearchFilter("id", Operator.GT, "1");
		users = userDao.findAll(DynamicSpecifications.bySearchFilter(Lists.newArrayList(filter), User.class));
		assertEquals(5, users.size());

		filter = new SearchFilter("id", Operator.GT, "6");
		users = userDao.findAll(DynamicSpecifications.bySearchFilter(Lists.newArrayList(filter), User.class));
		assertEquals(0, users.size());

		// GTE
		filter = new SearchFilter("id", Operator.GTE, "1");
		users = userDao.findAll(DynamicSpecifications.bySearchFilter(Lists.newArrayList(filter), User.class));
		assertEquals(6, users.size());

		filter = new SearchFilter("id", Operator.GTE, "6");
		users = userDao.findAll(DynamicSpecifications.bySearchFilter(Lists.newArrayList(filter), User.class));
		assertEquals(1, users.size());

		// LT
		filter = new SearchFilter("id", Operator.LT, "6");
		users = userDao.findAll(DynamicSpecifications.bySearchFilter(Lists.newArrayList(filter), User.class));
		assertEquals(5, users.size());

		filter = new SearchFilter("id", Operator.LT, "1");
		users = userDao.findAll(DynamicSpecifications.bySearchFilter(Lists.newArrayList(filter), User.class));
		assertEquals(0, users.size());

		// LTE
		filter = new SearchFilter("id", Operator.LTE, "6");
		users = userDao.findAll(DynamicSpecifications.bySearchFilter(Lists.newArrayList(filter), User.class));
		assertEquals(6, users.size());

		filter = new SearchFilter("id", Operator.LTE, "1");
		users = userDao.findAll(DynamicSpecifications.bySearchFilter(Lists.newArrayList(filter), User.class));
		assertEquals(1, users.size());

		// Empty filters, select all
		users = userDao.findAll(DynamicSpecifications.bySearchFilter(new ArrayList<SearchFilter>(), User.class));
		assertEquals(6, users.size());

		users = userDao.findAll(DynamicSpecifications.bySearchFilter(null, User.class));
		assertEquals(6, users.size());

		// AND 2 Conditions
		SearchFilter filter1 = new SearchFilter("name", Operator.EQ, "管理员");
		SearchFilter filter2 = new SearchFilter("loginName", Operator.LIKE, "min");
		users = userDao.findAll(DynamicSpecifications.bySearchFilter(Lists.newArrayList(filter1, filter2), User.class));
		assertEquals(1, users.size());

		filter1 = new SearchFilter("name", Operator.EQ, "管理员");
		filter2 = new SearchFilter("loginName", Operator.LIKE, "user");
		users = userDao.findAll(DynamicSpecifications.bySearchFilter(Lists.newArrayList(filter1, filter2), User.class));
		assertEquals(0, users.size());

		// 2 conditions on same field
		filter1 = new SearchFilter("id", Operator.GTE, "1");
		filter2 = new SearchFilter("id", Operator.LTE, "6");

		users = userDao.findAll(DynamicSpecifications.bySearchFilter(Lists.newArrayList(filter1, filter2), User.class));
		assertEquals(6, users.size());

		// Nest Attribute
		filter = new SearchFilter("team.id", Operator.EQ, "1");
		users = userDao.findAll(DynamicSpecifications.bySearchFilter(Lists.newArrayList(filter), User.class));
		assertEquals(6, users.size());

		filter = new SearchFilter("team.id", Operator.EQ, "10");
		users = userDao.findAll(DynamicSpecifications.bySearchFilter(Lists.newArrayList(filter), User.class));
		assertEquals(0, users.size());
	}
}
