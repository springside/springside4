package org.springside.examples.quickstart.repository;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ContextConfiguration;
import org.springside.examples.quickstart.entity.Task;
import org.springside.modules.test.spring.SpringTransactionalTestCase;

@ContextConfiguration(locations = { "/applicationContext.xml" })
public class TaskDaoTest extends SpringTransactionalTestCase {

	@Autowired
	private TaskDao taskDao;

	@Test
	public void findTasksByUserId() throws Exception {
		List<Task> tasks = taskDao.findByUserId(2L, new Sort(Direction.ASC, "id"));
		assertEquals(5, tasks.size());
		assertEquals(new Long(1), tasks.get(0).getId());

		tasks = taskDao.findByUserId(99999L, new Sort(Direction.ASC, "id"));
		assertEquals(0, tasks.size());
	}
}
