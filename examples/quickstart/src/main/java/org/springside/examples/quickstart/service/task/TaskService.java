package org.springside.examples.quickstart.service.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springside.examples.quickstart.entity.Task;
import org.springside.examples.quickstart.repository.TaskDao;

//Spring Bean的标识.
@Component
//默认将类中的所有public函数纳入事务管理.
@Transactional(readOnly = true)
public class TaskService {

	private TaskDao taskDao;

	public Task getTask(Long id) {
		return taskDao.findOne(id);
	}

	@Transactional(readOnly = false)
	public void saveTask(Task entity) {
		taskDao.save(entity);
	}

	@Transactional(readOnly = false)
	public void deleteTask(Long id) {
		taskDao.delete(id);
	}

	public List<Task> getAllTask() {
		return (List<Task>) taskDao.findAll(new Sort(Direction.ASC, "id"));
	}

	public List<Task> getUserTask(Long userId) {
		return taskDao.findByUserId(userId, new Sort(Direction.ASC, "id"));
	}

	@Autowired
	public void setTaskDao(TaskDao taskDao) {
		this.taskDao = taskDao;
	}
}
