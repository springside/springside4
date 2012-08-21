package org.springside.examples.quickstart.repository;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ContextConfiguration;
import org.springside.examples.quickstart.entity.Task;
import org.springside.modules.test.spring.SpringTransactionalTestCase;

@ContextConfiguration(locations = { "/applicationContext.xml" })
public class TasdkDaoTest extends SpringTransactionalTestCase {

	@Autowired
	private TaskDao taskDao;

	@PersistenceContext
	private EntityManager em;

	@Test
	public void findTasksByUserId() throws Exception {
		List<Task> tasks = taskDao.findByUserId(1L, new Sort(Direction.ASC, "id"));
		assertEquals(5, tasks.size());
		assertEquals(new Long(1), tasks.get(0).getId());
	}
}
