package org.springside.examples.miniweb.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springside.examples.miniweb.entity.Task;

public interface TaskDao extends PagingAndSortingRepository<Task, Long> {

}
