package org.springside.examples.bootservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springside.examples.bootservice.domain.Task;
import org.springside.examples.bootservice.repository.TaskDao;

// Spring Bean的标识.
@Component
// 类中所有public函数都纳入事务管理的标识.
@Transactional
public class TaskService {

	@Autowired
	private TaskDao taskDao;

	public List<Task> getAllTask() {
		return (List<Task>) taskDao.findAll();
	}

	public Task getTask(Long id) {
		return taskDao.findOne(id);
	}

	public void saveTask(Task task) {
		taskDao.save(task);
	}

	public void deleteTask(Long id) {
		taskDao.delete(id);
	}
}
