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
import org.springside.examples.bootapi.domain.Message;
import org.springside.examples.bootapi.repository.BookDao;
import org.springside.examples.bootapi.repository.MessageDao;

// Spring Bean的标识.
@Service
public class BookBorrowService {

	private static Logger logger = LoggerFactory.getLogger(BookBorrowService.class);

	@Autowired
	private BookDao bookDao;

	@Autowired
	private MessageDao messageDao;

	@Transactional
	public void applyBorrowRequest(Long id, Account borrower) {
		Book book = bookDao.findOne(id);

		if (!book.status.equals(Book.STATUS_IDLE)) {
			throw new RestException("The book is not idle", HttpStatus.BAD_REQUEST);
		}

		if (borrower.id.equals(book.owner.id)) {
			throw new RestException("User shouldn't borrower the book which is himeself", HttpStatus.BAD_REQUEST);
		}

		book.status = Book.STATUS_REQUEST;
		book.borrower = borrower;
		bookDao.save(book);

		Message message = new Message(book.owner,
				String.format("Apply book <%s> request by %s", book.title, borrower.name));

		messageDao.save(message);
	}

	@Transactional
	public void cancleBorrowRequest(Long id, Account borrower) {
		Book book = bookDao.findOne(id);

		if (!book.status.equals(Book.STATUS_REQUEST)) {
			throw new RestException("The book is not requesting", HttpStatus.BAD_REQUEST);
		}

		if (!borrower.id.equals(book.borrower.id)) {
			throw new RestException("User can't cancel other ones request", HttpStatus.FORBIDDEN);
		}

		book.status = Book.STATUS_IDLE;
		book.borrower = null;
		bookDao.save(book);

		Message message = new Message(book.owner,
				String.format("Cancel book <%s> request by %s", book.title, borrower.name));

		messageDao.save(message);

	}

	@Transactional
	public void markBookBorrowed(Long id, Account owner) {
		Book book = bookDao.findOne(id);

		if (!book.status.equals(Book.STATUS_REQUEST)) {
			throw new RestException("The book is not requesting", HttpStatus.BAD_REQUEST);
		}

		if (!owner.id.equals(book.owner.id)) {
			throw new RestException("User can't cofirm others book", HttpStatus.FORBIDDEN);
		}

		book.status = Book.STATUS_OUT;
		book.borrowDate = new Date();
		bookDao.save(book);

		Message message = new Message(book.borrower,
				String.format("Confirm book <%s> request by %s", book.title, owner.name));
		messageDao.save(message);
	}

	@Transactional
	public void rejectBorrowRequest(Long id, Account owner) {
		Book book = bookDao.findOne(id);

		if (!book.status.equals(Book.STATUS_REQUEST)) {
			throw new RestException("The book is not requesting", HttpStatus.BAD_REQUEST);
		}

		if (!owner.id.equals(book.owner.id)) {
			throw new RestException("User can't reject others book", HttpStatus.FORBIDDEN);
		}

		book.status = Book.STATUS_IDLE;
		book.borrowDate = null;
		book.borrower = null;
		bookDao.save(book);

		Message message = new Message(book.borrower,
				String.format("Reject book <%s> request by %s", book.title, owner.name));
		messageDao.save(message);

	}

	@Transactional
	public void markBookReturned(Long id, Account owner) {
		Book book = bookDao.findOne(id);

		if (!book.status.equals(Book.STATUS_OUT)) {
			throw new RestException("The book is not borrowing", HttpStatus.BAD_REQUEST);
		}

		if (!owner.id.equals(book.owner.id)) {
			throw new RestException("User can't make others book returned", HttpStatus.FORBIDDEN);
		}

		book.status = Book.STATUS_IDLE;
		book.borrowDate = null;
		book.borrower = null;
		bookDao.save(book);

		Message message = new Message(book.borrower,
				String.format("Mark book <%s> returned by %s", book.title, owner.name));
		messageDao.save(message);
	}

	@Transactional(readOnly = true)
	public List<Book> listMyBorrowedBook(Long borrowerId) {
		return bookDao.findByBorrowerId(borrowerId);
	}
}
