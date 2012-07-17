package org.springside.examples.quickstart.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springside.examples.quickstart.entity.Task;

public interface TaskDao extends PagingAndSortingRepository<Task, Long> {

}
