package org.springside.examples.quickstart.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springside.examples.quickstart.entity.Task;

public interface TaskDao extends PagingAndSortingRepository<Task, Long> {
	List<Task> findByUserId(Long id, Sort sort);
}
