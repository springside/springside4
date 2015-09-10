package org.springside.examples.bootservice.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springside.examples.bootservice.BootServiceApplication;
import org.springside.examples.bootservice.domain.Task;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BootServiceApplication.class)
@DirtiesContext
public class TaskDaoTest {

	@Autowired
	private TaskDao taskDao;

	@Test
	public void findByUserId() {
		List<Task> tasks = taskDao.findByUserId(2L);
		assertThat(tasks).hasSize(5);
		assertThat(tasks.get(0).title).isEqualTo("Spring Boot");
	}
}
