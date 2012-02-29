package org.springside.examples.showcase.common.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springside.examples.showcase.common.entity.User;

public interface UserJpaDao extends PagingAndSortingRepository<User, Long> {

	User findByName(String name);

	User findByLoginName(String loginName);
}
