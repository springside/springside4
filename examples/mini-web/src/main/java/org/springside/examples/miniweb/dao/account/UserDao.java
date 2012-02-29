package org.springside.examples.miniweb.dao.account;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springside.examples.miniweb.entity.account.User;

/**
 * 用户对象的Dao interface.
 * 
 * @author calvin
 */
public interface UserDao extends PagingAndSortingRepository<User, Long> {

	User findByLoginName(String loginName);
}
