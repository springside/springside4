package org.springside.examples.bootapi.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springside.examples.bootapi.domain.Book;

/**
 * 基于Spring Data JPA的Dao接口, 自动根据接口生成实现.
 * 
 * PagingAndSortingRepository默认有针对实体对象的CRUD与分页查询函数.
 * 
 * Spring Data JPA 还会解释新增方法名生成新方法的实现.
 */
public interface BookDao extends PagingAndSortingRepository<Book, Long> {

	List<Book> findByOwnerId(Long ownerId, Pageable pageable);

	List<Book> findByBorrowerId(Long borrowerId, Pageable pageable);
}
