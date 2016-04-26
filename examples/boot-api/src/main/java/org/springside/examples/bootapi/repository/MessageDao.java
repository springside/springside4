package org.springside.examples.bootapi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springside.examples.bootapi.domain.Message;

/**
 * 基于Spring Data JPA的Dao接口, 自动根据接口生成实现.
 * 
 * CrudRepository默认有针对实体对象的CRUD方法.
 */
public interface MessageDao extends CrudRepository<Message, Long> {
}
