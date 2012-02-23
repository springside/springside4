package org.springside.modules.orm.hibernate;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springside.modules.orm.hibernate.SimpleHibernateDao;
import org.springside.modules.orm.hibernate.data.User;
import org.springside.modules.test.data.Fixtures;
import org.springside.modules.test.spring.SpringTxTestCase;
import org.springside.modules.utils.Reflections;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@ContextConfiguration(locations = { "/applicationContext-core-test.xml" })
public class SimpleHibernateDaoTest extends SpringTxTestCase {

	private static final String DEFAULT_LOGIN_NAME = "admin";

	private SimpleHibernateDao<User, Long> dao;

	@Autowired
	private SessionFactory sessionFactory;

	@Before
	public void setUp() throws Exception {
		simpleJdbcTemplate.update("drop all objects");

		executeSqlScript("classpath:/schema.sql", false);

		Fixtures.loadData((DataSource) applicationContext.getBean("dataSource"), "classpath:/test-data.xml");

		dao = new SimpleHibernateDao<User, Long>(User.class);
		dao.setSessionFactory(sessionFactory);
	}

	@Test
	public void crud() {
		User user = new User();
		user.setName("foo");
		user.setLoginName("foo");
		//add
		dao.save(user);
		dao.flush();
		//update
		user.setName("boo");
		dao.save(user);
		dao.flush();

		//delete object
		dao.delete(user);
		dao.flush();

		//delete by id
		User user2 = new User();
		user2.setName("foo2");
		user2.setLoginName("foo2");
		dao.save(user2);
		dao.flush();
		dao.delete(user2.getId());
		dao.flush();
	}

	@Test
	public void getSome() {
		//get all
		List<User> users = dao.getAll();
		assertEquals(6, users.size());

		//get all with order
		users = dao.getAll("id", true);
		assertEquals(6, users.size());
		assertEquals(DEFAULT_LOGIN_NAME, users.get(0).getLoginName());

		//get by id list
		users = dao.get(Lists.newArrayList(1L, 2L));
		assertEquals(2, users.size());
	}

	@Test
	public void findByProperty() {
		List<User> users = dao.findBy("loginName", DEFAULT_LOGIN_NAME);
		assertEquals(1, users.size());
		assertEquals(DEFAULT_LOGIN_NAME, users.get(0).getLoginName());

		User user = dao.findUniqueBy("loginName", DEFAULT_LOGIN_NAME);
		assertEquals(DEFAULT_LOGIN_NAME, user.getLoginName());
	}

	@Test
	public void findByHQL() {

		List<User> users = dao.find("from User u where loginName=?", DEFAULT_LOGIN_NAME);
		assertEquals(1, users.size());
		assertEquals(DEFAULT_LOGIN_NAME, users.get(0).getLoginName());

		User user = dao.findUnique("from User u where loginName=?", DEFAULT_LOGIN_NAME);
		assertEquals(DEFAULT_LOGIN_NAME, user.getLoginName());

		Map<String, Object> values = Maps.newHashMap();
		values.put("loginName", DEFAULT_LOGIN_NAME);
		users = dao.find("from User u where loginName=:loginName", values);
		assertEquals(1, users.size());
		assertEquals(DEFAULT_LOGIN_NAME, users.get(0).getLoginName());

		user = dao.findUnique("from User u where loginName=:loginName", values);
		assertEquals(DEFAULT_LOGIN_NAME, user.getLoginName());
	}

	@Test
	public void findByCriterion() {
		Criterion c = Restrictions.eq("loginName", DEFAULT_LOGIN_NAME);
		List<User> users = dao.find(c);
		assertEquals(1, users.size());
		assertEquals(DEFAULT_LOGIN_NAME, users.get(0).getLoginName());

		User user = dao.findUnique(c);
		assertEquals(DEFAULT_LOGIN_NAME, user.getLoginName());
	}

	@Test
	public void batchUpdate() {
		Map map = new HashMap();
		map.put("ids", new Long[] { 1L, 23L });

		dao.batchExecute("update User u set u.status='disabled' where id in(:ids)", map);
		User u1 = dao.get(1L);
		assertEquals("disabled", u1.getStatus());
		User u3 = dao.get(3L);
		assertEquals("enabled", u3.getStatus());
	}

	@Test
	public void eagerFetch() {
		String sql = "from User u left join fetch u.roleList order by u.id";

		Query query = dao.createQuery(sql);
		List<User> userList = dao.distinct(query).list();
		assertEquals(6, userList.size());
		assertTrue(Hibernate.isInitialized(userList.get(0).getRoleList()));

		Criteria criteria = dao.createCriteria().setFetchMode("roles", FetchMode.JOIN);
		userList = dao.distinct(criteria).list();
		assertEquals(6, userList.size());
		assertTrue(Hibernate.isInitialized(userList.get(0).getRoleList()));
	}

	@Test
	public void misc() {
		getIdName();
		isPropertyUnique();
		constructor();
	}

	public void getIdName() {
		assertEquals("id", dao.getIdName());
	}

	public void isPropertyUnique() {
		assertEquals(true, dao.isPropertyUnique("loginName", "admin", "admin"));
		assertEquals(true, dao.isPropertyUnique("loginName", "user6", "admin"));
		assertEquals(false, dao.isPropertyUnique("loginName", "user2", "admin"));
	}

	public void constructor() {
		MyUserDao myDao = new MyUserDao();
		Class entityClazz = (Class) Reflections.getFieldValue(myDao, "entityClass");
		assertEquals(User.class, entityClazz);
	}

	static class MyUserDao extends SimpleHibernateDao<User, Long> {
	}
}
