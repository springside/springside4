/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.quickstart.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springside.examples.quickstart.entity.Task;

public interface TaskDao extends PagingAndSortingRepository<Task, Long>, JpaSpecificationExecutor<Task> {

	Page<Task> findByUserId(Long id, Pageable pageRequest);

	@Modifying
	@Query("delete from Task task where task.user.id=?1")
	void deleteByUserId(Long id);
}
