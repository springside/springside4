package org.springside.examples.miniweb.dao.account;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springside.examples.miniweb.entity.account.Group;

/**
 * 权限组对象的Dao interface.
 * 
 * @author calvin
 */

public interface GroupDao extends PagingAndSortingRepository<Group, Long>, GroupDaoCustom {
}
