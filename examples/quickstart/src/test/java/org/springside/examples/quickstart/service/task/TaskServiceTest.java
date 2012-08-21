package org.springside.examples.quickstart.service.task;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springside.examples.quickstart.repository.TaskDao;
import org.springside.examples.quickstart.service.task.TaskService;

/**
 * TaskService的测试用例, 测试Service层的业务逻辑.
 * 
 * @author calvin
 */
public class TaskServiceTest {

	private TaskService taskService;
	@Mock
	private TaskDao mockTaskDao;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		taskService = new TaskService();
		taskService.setTaskDao(mockTaskDao);
	}

}
