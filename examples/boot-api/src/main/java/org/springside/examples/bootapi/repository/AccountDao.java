package org.springside.examples.bootapi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springside.examples.bootapi.domain.Account;

/**
 * 基于Spring Data JPA的Dao接口, 自动根据接口生成实现.
 * 
 * CrudRepository默认有针对实体对象的CRUD方法.
 * 
 * Spring Data JPA 还会解释新增方法名生成新方法的实现.
 */
public interface AccountDao extends CrudRepository<Account, Long> {

	Account findByEmail(String email);
}
