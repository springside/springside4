package org.springside.examples.bootapi.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springside.examples.bootapi.domain.Book;

/**
 * 基于Spring Data JPA的Dao接口.
 * 
 * PagingAndSortingRepository默认有针对实体对象的CRUD与分页查询函数.
 * 
 * 而扩展函数仅需定义函数名，Spring Data JPA自动根据方法名生成实现.
 */
public interface BookDao extends PagingAndSortingRepository<Book, Long> {

	List<Book> findByOwnerId(Long ownerId);

	List<Book> findByBorrowerId(Long borrowerId);
}
