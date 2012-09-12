package org.springside.examples.quickstart.repository;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ContextConfiguration;
import org.springside.examples.quickstart.entity.Task;
import org.springside.modules.persistence.ByWebFilterSpecification;
import org.springside.modules.persistence.SearchFilter;
import org.springside.modules.persistence.SearchFilter.Operator;
import org.springside.modules.test.spring.SpringTransactionalTestCase;

import com.google.common.collect.Lists;

@ContextConfiguration(locations = { "/applicationContext.xml" })
public class TaskDaoTest extends SpringTransactionalTestCase {

	@Autowired
	private TaskDao taskDao;

	@Autowired
	private ByWebFilterSpecification spec;

	@Test
	public void findTasksByUserId() throws Exception {
		Page<Task> tasks = taskDao.findByUserId(2L, new PageRequest(0, 100, Direction.ASC, "id"));
		assertEquals(5, tasks.getContent().size());
		assertEquals(new Long(1), tasks.getContent().get(0).getId());

		tasks = taskDao.findByUserId(99999L, new PageRequest(0, 100, Direction.ASC, "id"));
		assertEquals(0, tasks.getContent().size());
	}

	@Test
	public void fineTasksByFilter() {
		//EQ
		SearchFilter filter = new SearchFilter("title", "Study PlayFramework 2.0", Operator.EQ);
		List<Task> tasks = taskDao.findAll(spec.byWebFilter(Lists.newArrayList(filter), Task.class));
		assertEquals(1, tasks.size());
		//LIKE
		filter = new SearchFilter("description", "playframework", Operator.LIKE);
		tasks = taskDao.findAll(spec.byWebFilter(Lists.newArrayList(filter), Task.class));
		assertEquals(1, tasks.size());
		//GT
		filter = new SearchFilter("id", 1L, Operator.GT);
		tasks = taskDao.findAll(spec.byWebFilter(Lists.newArrayList(filter), Task.class));
		assertEquals(4, tasks.size());

		filter = new SearchFilter("id", 5L, Operator.GT);
		tasks = taskDao.findAll(spec.byWebFilter(Lists.newArrayList(filter), Task.class));
		assertEquals(0, tasks.size());
		//GTE
		filter = new SearchFilter("id", 1L, Operator.GTE);
		tasks = taskDao.findAll(spec.byWebFilter(Lists.newArrayList(filter), Task.class));
		assertEquals(5, tasks.size());

		filter = new SearchFilter("id", 5L, Operator.GTE);
		tasks = taskDao.findAll(spec.byWebFilter(Lists.newArrayList(filter), Task.class));
		assertEquals(1, tasks.size());

		//LT
		filter = new SearchFilter("id", 5L, Operator.LT);
		tasks = taskDao.findAll(spec.byWebFilter(Lists.newArrayList(filter), Task.class));
		assertEquals(4, tasks.size());

		filter = new SearchFilter("id", 1L, Operator.LT);
		tasks = taskDao.findAll(spec.byWebFilter(Lists.newArrayList(filter), Task.class));
		assertEquals(0, tasks.size());
		//LTE
		filter = new SearchFilter("id", 5L, Operator.LTE);
		tasks = taskDao.findAll(spec.byWebFilter(Lists.newArrayList(filter), Task.class));
		assertEquals(5, tasks.size());

		filter = new SearchFilter("id", 1L, Operator.LTE);
		tasks = taskDao.findAll(spec.byWebFilter(Lists.newArrayList(filter), Task.class));
		assertEquals(1, tasks.size());

		//AND 2 Conditions
		SearchFilter filter1 = new SearchFilter("title", "Study PlayFramework 2.0", Operator.EQ);
		SearchFilter filter2 = new SearchFilter("description", "playframework", Operator.LIKE);
		tasks = taskDao.findAll(spec.byWebFilter(Lists.newArrayList(filter1, filter2), Task.class));
		assertEquals(1, tasks.size());

		filter1 = new SearchFilter("title", "Study PlayFramework 2.0", Operator.EQ);
		filter2 = new SearchFilter("description", "springfuse", Operator.LIKE);
		tasks = taskDao.findAll(spec.byWebFilter(Lists.newArrayList(filter1, filter2), Task.class));
		assertEquals(0, tasks.size());
	}
}
