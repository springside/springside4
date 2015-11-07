package org.springside.examples.bootapi.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springside.examples.bootapi.api.support.RestException;
import org.springside.examples.bootapi.domain.Account;
import org.springside.examples.bootapi.domain.Book;
import org.springside.examples.bootapi.repository.BookDao;

// Spring Bean的标识.
@Service
public class BookAdminService {

	private static Logger logger = LoggerFactory.getLogger(BookBorrowService.class);

	@Autowired
	private BookDao bookDao;

	@Transactional(readOnly = true)
	public Iterable<Book> findAll() {
		return bookDao.findAll();
	}

	@Transactional(readOnly = true)
	public Book findOne(Long id) {
		return bookDao.findOne(id);
	}

	@Transactional(readOnly = true)
	public List<Book> listMyBook(Long ownerId) {
		return bookDao.findByOwnerId(ownerId);
	}

	@Transactional
	public void saveBook(Book book, Account owner) {

		book.owner = owner;
		book.status = Book.STATUS_IDLE;
		book.onboardDate = new Date();

		bookDao.save(book);
	}

	@Transactional
	public void modifyBook(Book book, Long currentAccountId) {
		if (!currentAccountId.equals(book.owner.id)) {
			throw new RestException("User can't modify others book", HttpStatus.FORBIDDEN);
		}

		Book orginalBook = bookDao.findOne(book.id);
		orginalBook.title = book.title;
		orginalBook.url = book.url;
		bookDao.save(orginalBook);
	}

	@Transactional
	public void deleteBook(Long id, Long currentAccountId) {
		Book book = bookDao.findOne(id);

		if (!currentAccountId.equals(book.owner.id)) {
			throw new RestException("User can't delete others book", HttpStatus.FORBIDDEN);
		}

		bookDao.delete(id);
	}
}
