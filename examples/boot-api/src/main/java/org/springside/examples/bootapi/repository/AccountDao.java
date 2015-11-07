package org.springside.examples.bootapi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springside.examples.bootapi.domain.Account;

/**
 * 基于Spring Data JPA的Dao接口.
 * 
 * CrudRepository默认有针对实体对象的CRUD函数.
 * 
 * 而扩展函数仅需定义函数名，Spring Data JPA自动根据方法名生成实现.
 */
public interface AccountDao extends CrudRepository<Account, Long> {

	Account findByEmail(String email);
}
