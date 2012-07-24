package org.springside.examples.quickstart.service;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springside.examples.quickstart.dao.TaskDao;
import org.springside.examples.quickstart.service.TaskManager;

/**
 * TaskManager的测试用例, 测试Service层的业务逻辑.
 * 
 * @author calvin
 */
public class TaskManagerTest {

	private TaskManager taskManager;
	@Mock
	private TaskDao mockTaskDao;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		taskManager = new TaskManager();
		taskManager.setTaskDao(mockTaskDao);
	}

}
