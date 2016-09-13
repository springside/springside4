package org.springside.examples.bootapi.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springside.examples.bootapi.domain.Account;
import org.springside.examples.bootapi.domain.Book;
import org.springside.examples.bootapi.repository.BookDao;
import org.springside.examples.bootapi.service.exception.ErrorCode;
import org.springside.examples.bootapi.service.exception.ServiceException;
import org.springside.modules.utils.Clock;

// Spring Bean的标识.
@Service
public class BookAdminService {

	private static Logger logger = LoggerFactory.getLogger(BookBorrowService.class);

	@Autowired
	private BookDao bookDao;

	// 可注入的Clock，方便测试时控制日期
	protected Clock clock = Clock.DEFAULT;

	@Transactional(readOnly = true)
	public Iterable<Book> findAll(Pageable pageable) {
		return bookDao.findAll(pageable);
	}

	@Transactional(readOnly = true)
	public Book findOne(Long id) {
		return bookDao.findOne(id);
	}

	@Transactional(readOnly = true)
	public List<Book> listMyBook(Long ownerId, Pageable pageable) {
		return bookDao.findByOwnerId(ownerId, pageable);
	}

	@Transactional
	public void saveBook(Book book, Account owner) {

		book.owner = owner;
		book.status = Book.STATUS_IDLE;
		book.onboardDate = clock.getCurrentDate();

		bookDao.save(book);
	}

	@Transactional
	public void modifyBook(Book book, Long currentAccountId) {
		if (!currentAccountId.equals(book.owner.id)) {
			logger.error("user:" + currentAccountId + " try to modified a book:" + book.id + " which is not him");
			throw new ServiceException("User can't modify others book", ErrorCode.BOOK_OWNERSHIP_WRONG);
		}

		Book orginalBook = bookDao.findOne(book.id);

		if (orginalBook == null) {
			logger.error("user:" + currentAccountId + " try to modified a book:" + book.id + " which is not exist");
			throw new ServiceException("The Book is not exist", ErrorCode.BAD_REQUEST);
		}

		orginalBook.title = book.title;
		orginalBook.url = book.url;
		bookDao.save(orginalBook);
	}

	@Transactional
	public void deleteBook(Long id, Long currentAccountId) {
		Book book = bookDao.findOne(id);

		if (book == null) {
			logger.error("user:" + currentAccountId + " try to delete a book:" + id + " which is not exist");
			throw new ServiceException("The Book is not exist", ErrorCode.BAD_REQUEST);
		}

		if (!currentAccountId.equals(book.owner.id)) {
			logger.error("user:" + currentAccountId + " try to delete a book:" + book.id + " which is not him");
			throw new ServiceException("User can't delete others book", ErrorCode.BOOK_OWNERSHIP_WRONG);
		}

		bookDao.delete(id);
	}
}
