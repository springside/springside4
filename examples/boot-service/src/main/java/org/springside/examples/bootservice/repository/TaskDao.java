package org.springside.examples.bootservice.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springside.examples.bootservice.domain.Task;

/**
 * 基于Spring Data JPA的Dao接口。
 */
public interface TaskDao extends CrudRepository<Task, Long> {

	List<Task> findByUserId(Long id);
}
