package org.springside.examples.showcase.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springside.examples.showcase.entity.Role;

public interface RoleDao extends PagingAndSortingRepository<Role, Long> {

}
