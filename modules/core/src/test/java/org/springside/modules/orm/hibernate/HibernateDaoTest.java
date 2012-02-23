package org.springside.modules.orm.hibernate;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;
import org.springside.modules.orm.PageRequest.Sort;
import org.springside.modules.orm.PropertyFilter;
import org.springside.modules.orm.hibernate.HibernateDao;
import org.springside.modules.orm.hibernate.data.User;
import org.springside.modules.test.data.Fixtures;
import org.springside.modules.test.spring.SpringTxTestCase;

import com.google.common.collect.Lists;

@ContextConfiguration(locations = { "/applicationContext-core-test.xml" })
public class HibernateDaoTest extends SpringTxTestCase {

	private HibernateDao<User, Long> dao;

	@Autowired
	private SessionFactory sessionFactory;

	@Before
	public void setUp() throws Exception {
		simpleJdbcTemplate.update("drop all objects");

		executeSqlScript("classpath:/schema.sql", false);

		Fixtures.loadData((DataSource) applicationContext.getBean("dataSource"), "/test-data.xml");

		dao = new HibernateDao<User, Long>(User.class);
		dao.setSessionFactory(sessionFactory);
	}

	@Test
	public void getAll() {
		//初始化数据中共有6个用户
		PageRequest pageRequest = new PageRequest(1, 5);
		Page<User> page = dao.getAll(pageRequest);
		assertEquals(5, page.getResult().size());

		//自动统计总数
		assertEquals(6L, page.getTotalItems());

		pageRequest.setPageNo(2);
		page = dao.getAll(pageRequest);
		assertEquals(1, page.getResult().size());
	}

	@Test
	public void findByHQL() {
		//初始化数据中共有6个email为@springside.org.cn的用户
		PageRequest pageRequest = new PageRequest(1, 5);
		Page<User> page = dao.findPage(pageRequest, "from User u where email like ?", "%springside.org.cn%");
		assertEquals(5, page.getResult().size());

		//自动统计总数
		assertEquals(6L, page.getTotalItems());

		//翻页
		pageRequest.setPageNo(2);
		page = dao.findPage(pageRequest, "from User u where email like ?", "%springside.org.cn%");
		assertEquals(1, page.getResult().size());

		//命名参数版本
		Map<String, String> paraMap = Collections.singletonMap("email", "%springside.org.cn%");
		pageRequest = new PageRequest(1, 5);
		page = dao.findPage(pageRequest, "from User u where email like :email", paraMap);
		assertEquals(5, page.getResult().size());

		//自动统计总数
		assertEquals(6L, page.getTotalItems());

		//翻页
		pageRequest.setPageNo(2);
		page = dao.findPage(pageRequest, "from User u where email like :email", paraMap);
		assertEquals(1, page.getResult().size());
	}

	@Test
	public void findByCriterion() {
		//初始化数据中共有6个email为@springside.org.cn的用户
		PageRequest pageRequest = new PageRequest(1, 5);
		Criterion c = Restrictions.like("email", "springside.org.cn", MatchMode.ANYWHERE);
		Page<User> page = dao.findPage(pageRequest, c);
		assertEquals(5, page.getResult().size());

		//自动统计总数
		assertEquals(6L, page.getTotalItems());

		//翻页
		pageRequest.setPageNo(2);
		page = dao.findPage(pageRequest, c);
		assertEquals(1, page.getResult().size());
	}

	@Test
	public void findByCriterionWithOrder() {
		//初始化数据中共有6个email为@springside.org.cn的用户
		PageRequest pageRequest = new PageRequest(1, 5);
		pageRequest.setOrderBy("name,loginName");
		pageRequest.setOrderDir(Sort.DESC + "," + Sort.ASC);

		Criterion c = Restrictions.like("email", "springside.org.cn", MatchMode.ANYWHERE);
		Page<User> page = dao.findPage(pageRequest, c);

		assertEquals("Sawyer", page.getResult().get(0).getName());
	}

	@Test
	public void findByProperty() {
		List<User> users = dao.findBy("loginName", "admin", PropertyFilter.MatchType.EQ);
		assertEquals(1, users.size());
		assertEquals("admin", users.get(0).getLoginName());

		users = dao.findBy("email", "springside.org.cn", PropertyFilter.MatchType.LIKE);
		assertEquals(6, users.size());
		assertTrue(users.get(0).getEmail().indexOf("springside.org.cn") != -1);
	}

	@Test
	public void findByFilters() {
		List<PropertyFilter> filters;
		//EQ filter
		PropertyFilter eqFilter = new PropertyFilter("EQS_loginName", "admin");
		filters = Lists.newArrayList(eqFilter);

		List<User> users = dao.find(filters);
		assertEquals(1, users.size());
		assertEquals("admin", users.get(0).getLoginName());

		//LIKE filter and OR
		PropertyFilter likeAndOrFilter = new PropertyFilter("LIKES_email_OR_loginName", "springside.org.cn");
		filters = Lists.newArrayList(likeAndOrFilter);

		users = dao.find(filters);
		assertEquals(6, users.size());
		assertTrue(StringUtils.contains(users.get(0).getEmail(), "springside.org.cn"));

		//Filter with Page
		PageRequest pageRequest = new PageRequest(1, 5);
		Page<User> page = dao.findPage(pageRequest, filters);
		assertEquals(5, page.getResult().size());
		assertEquals(6L, page.getTotalItems());

		pageRequest.setPageNo(2);
		page = dao.findPage(pageRequest, filters);
		assertEquals(1, page.getResult().size());

		//Date and LT/GT filter
		PropertyFilter dateLtFilter = new PropertyFilter("LTD_createTime", "2046-01-01");
		filters = Lists.newArrayList(dateLtFilter);
		users = dao.find(filters);
		assertEquals(6, users.size());

		PropertyFilter dateGtFilter = new PropertyFilter("GTD_createTime", "2046-01-01 10:00:22");
		filters = Lists.newArrayList(dateGtFilter);
		users = dao.find(filters);
		assertEquals(0, users.size());
	}

	@Test
	public void findPageByHqlAutoCount() {
		PageRequest pageRequest = new PageRequest(1, 5);
		Page<User> page = dao.findPage(pageRequest, "from User user");
		assertEquals(6L, page.getTotalItems());

		page = dao.findPage(pageRequest, "select user from User user");
		assertEquals(6L, page.getTotalItems());

		page = dao.findPage(pageRequest, "select user from User user order by id");
		assertEquals(6L, page.getTotalItems());

		page = dao.findPage(pageRequest, "select user from User user where user.id=1 order by id");
		assertEquals(1L, page.getTotalItems());
	}
}
