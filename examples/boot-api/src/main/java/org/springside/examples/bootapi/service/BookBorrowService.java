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
import org.springside.examples.bootapi.domain.Message;
import org.springside.examples.bootapi.repository.BookDao;
import org.springside.examples.bootapi.repository.MessageDao;
import org.springside.examples.bootapi.service.exception.ErrorCode;
import org.springside.examples.bootapi.service.exception.ServiceException;
import org.springside.modules.utils.Clock;

// Spring Bean的标识.
@Service
public class BookBorrowService {

	private static Logger logger = LoggerFactory.getLogger(BookBorrowService.class);

	@Autowired
	protected BookDao bookDao;

	@Autowired
	protected MessageDao messageDao;

	// 可注入的Clock，方便测试时控制日期
	protected Clock clock = Clock.DEFAULT;

	@Transactional
	public void applyBorrowRequest(Long id, Account borrower) {
		Book book = bookDao.findOne(id);

		if (!book.status.equals(Book.STATUS_IDLE)) {
			logger.error("User request the book not idle, user id:" + borrower.id + ",book id:" + id + ",status:"
					+ book.status);
			throw new ServiceException("The book is not idle", ErrorCode.BOOK_STATUS_WRONG);
		}

		if (borrower.id.equals(book.owner.id)) {
			logger.error("User borrow the book himself, user id:" + borrower.id + ",book id:" + id);
			throw new ServiceException("User shouldn't borrower the book which is himeself",
					ErrorCode.BOOK_OWNERSHIP_WRONG);
		}

		book.status = Book.STATUS_REQUEST;
		book.borrower = borrower;
		bookDao.save(book);

		Message message = new Message(book.owner,
				String.format("Apply book <%s> request by %s", book.title, borrower.name), clock.getCurrentDate());

		messageDao.save(message);
	}

	@Transactional
	public void cancelBorrowRequest(Long id, Account borrower) {
		Book book = bookDao.findOne(id);

		if (!book.status.equals(Book.STATUS_REQUEST)) {
			logger.error("User cancel the book not reqesting, user id:" + borrower.id + ",book id:" + id + ",status:"
					+ book.status);
			throw new ServiceException("The book is not requesting", ErrorCode.BOOK_STATUS_WRONG);
		}

		if (!borrower.id.equals(book.borrower.id)) {
			logger.error("User cancel the book not request by him, user id:" + borrower.id + ",book id:" + id
					+ ",borrower id" + book.borrower.id);
			throw new ServiceException("User can't cancel other ones request", ErrorCode.BOOK_OWNERSHIP_WRONG);
		}

		book.status = Book.STATUS_IDLE;
		book.borrower = null;
		bookDao.save(book);

		Message message = new Message(book.owner,
				String.format("Cancel book <%s> request by %s", book.title, borrower.name), clock.getCurrentDate());

		messageDao.save(message);
	}

	@Transactional
	public void markBookBorrowed(Long id, Account owner) {
		Book book = bookDao.findOne(id);

		if (!book.status.equals(Book.STATUS_REQUEST)) {
			logger.error("User confirm the book not reqesting, user id:" + owner.id + ",book id:" + id + ",status:"
					+ book.status);
			throw new ServiceException("The book is not requesting", ErrorCode.BOOK_STATUS_WRONG);
		}

		if (!owner.id.equals(book.owner.id)) {
			logger.error("User confirm the book not himself, user id:" + owner.id + ",book id:" + id + ",owner id"
					+ book.owner.id);
			throw new ServiceException("User can't cofirm others book", ErrorCode.BOOK_OWNERSHIP_WRONG);
		}

		book.status = Book.STATUS_OUT;
		book.borrowDate = clock.getCurrentDate();
		bookDao.save(book);

		Message message = new Message(book.borrower,
				String.format("Confirm book <%s> request by %s", book.title, owner.name), clock.getCurrentDate());
		messageDao.save(message);
	}

	@Transactional
	public void rejectBorrowRequest(Long id, Account owner) {
		Book book = bookDao.findOne(id);

		if (!book.status.equals(Book.STATUS_REQUEST)) {
			logger.error("User reject the book not reqesting, user id:" + owner.id + ",book id:" + id + ",status:"
					+ book.status);
			throw new ServiceException("The book is not requesting", ErrorCode.BOOK_STATUS_WRONG);
		}

		if (!owner.id.equals(book.owner.id)) {

			logger.error("User reject the book not himself, user id:" + owner.id + ",book id:" + id + ",owener id"
					+ book.owner.id);
			throw new ServiceException("User can't reject others book", ErrorCode.BOOK_OWNERSHIP_WRONG);
		}

		book.status = Book.STATUS_IDLE;
		book.borrowDate = null;
		book.borrower = null;
		bookDao.save(book);

		Message message = new Message(book.borrower,
				String.format("Reject book <%s> request by %s", book.title, owner.name), clock.getCurrentDate());
		messageDao.save(message);
	}

	@Transactional
	public void markBookReturned(Long id, Account owner) {
		Book book = bookDao.findOne(id);

		if (!book.status.equals(Book.STATUS_OUT)) {
			logger.error(
					"User return the book not out, user id:" + owner.id + ",book id:" + id + ",status:" + book.status);
			throw new ServiceException("The book is not borrowing", ErrorCode.BOOK_STATUS_WRONG);
		}

		if (!owner.id.equals(book.owner.id)) {
			logger.error("User return the book not himself, user id:" + owner.id + ",book id:" + id + ",owner id"
					+ book.owner.id);
			throw new ServiceException("User can't make others book returned", ErrorCode.BOOK_OWNERSHIP_WRONG);
		}

		book.status = Book.STATUS_IDLE;
		book.borrowDate = null;
		book.borrower = null;
		bookDao.save(book);

		Message message = new Message(book.borrower,
				String.format("Mark book <%s> returned by %s", book.title, owner.name), clock.getCurrentDate());
		messageDao.save(message);
	}

	@Transactional(readOnly = true)
	public List<Book> listMyBorrowedBook(Long borrowerId, Pageable pageable) {
		return bookDao.findByBorrowerId(borrowerId, pageable);
	}
}
