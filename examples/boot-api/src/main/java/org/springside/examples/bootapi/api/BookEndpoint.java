package org.springside.examples.bootapi.api;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springside.examples.bootapi.api.support.RestException;
import org.springside.examples.bootapi.domain.Account;
import org.springside.examples.bootapi.domain.Book;
import org.springside.examples.bootapi.dto.AccountDto;
import org.springside.examples.bootapi.dto.BookDto;
import org.springside.examples.bootapi.service.BookAdminService;
import org.springside.examples.bootapi.service.BookBorrowService;
import org.springside.modules.mapper.BeanMapper;
import org.springside.modules.web.MediaTypes;

// Spring Restful MVC Controller的标识, 直接输出内容，不调用template引擎.
@RestController
public class BookEndpoint {

	private static Logger logger = LoggerFactory.getLogger(BookEndpoint.class);

	@Autowired
	private BookAdminService adminService;

	@Autowired
	private BookBorrowService borrowService;

	@RequestMapping(value = "/api/books", method = RequestMethod.GET, produces = MediaTypes.JSON_UTF_8)
	public List<BookDto> listAllBook() {
		Iterable<Book> books = adminService.findAll();

		return BeanMapper.mapList(books, BookDto.class);
	}

	@RequestMapping(value = "/api/books/{id}", method = RequestMethod.GET, produces = MediaTypes.JSON_UTF_8)
	public BookDto listOneBook(@PathVariable("id") Long id) {
		Book book = adminService.findOne(id);

		return BeanMapper.map(book, BookDto.class);
	}

	@RequestMapping(value = "/api/books", method = RequestMethod.POST, consumes = MediaTypes.JSON_UTF_8)
	public void createBook(@RequestBody BookDto bookDto, HttpSession session) {
		AccountDto currentUser = getCurrentAccount(session);
		adminService.save(BeanMapper.map(bookDto, Book.class), BeanMapper.map(currentUser, Account.class));
	}

	@RequestMapping(value = "/api/books/{id}/modify", method = RequestMethod.POST, consumes = MediaTypes.JSON_UTF_8)
	public void modifyBook(@RequestBody BookDto bookDto, HttpSession session) {
		AccountDto currentUser = getCurrentAccount(session);
		adminService.modifyBook(BeanMapper.map(bookDto, Book.class), currentUser.id);
	}

	@RequestMapping(value = "/api/books/{id}/delete")
	public void deleteBook(@PathVariable("id") Long id, HttpSession session) {
		AccountDto currentUser = getCurrentAccount(session);
		adminService.deleteBook(id, currentUser.id);
	}

	@RequestMapping(value = "/api/books/{id}/request")
	public void applyBorrowRequest(@PathVariable("id") Long id, HttpSession session) {
		AccountDto currentUser = getCurrentAccount(session);
		borrowService.applyBorrowRequest(id, BeanMapper.map(currentUser, Account.class));
	}

	@RequestMapping(value = "/api/books/{id}/cancel")
	public void cancelBorrowRequest(@PathVariable("id") Long id, HttpSession session) {
		AccountDto currentUser = getCurrentAccount(session);
		borrowService.cancleBorrowRequest(id, BeanMapper.map(currentUser, Account.class));
	}

	@RequestMapping(value = "/api/books/{id}/confirm")
	public void markBookBorrowed(@PathVariable("id") Long id, HttpSession session) {
		AccountDto currentUser = getCurrentAccount(session);
		borrowService.markBookBorrowed(id, BeanMapper.map(currentUser, Account.class));
	}

	@RequestMapping(value = "/api/books/{id}/reject")
	public void rejectBorrowRequest(@PathVariable("id") Long id, HttpSession session) {
		AccountDto currentUser = getCurrentAccount(session);

		borrowService.rejectBorrowRequest(id, BeanMapper.map(currentUser, Account.class));
	}

	@RequestMapping(value = "/api/books/{id}/return")
	public void markBookReturned(@PathVariable("id") Long id, HttpSession session) {
		AccountDto currentUser = getCurrentAccount(session);
		borrowService.markBookReturned(id, BeanMapper.map(currentUser, Account.class));
	}

	@RequestMapping(value = "/api/mybook", produces = MediaTypes.JSON_UTF_8)
	public List<BookDto> listMyBook(HttpSession session) {
		AccountDto currentUser = getCurrentAccount(session);
		List<Book> books = adminService.listMyBook(currentUser.id);
		return BeanMapper.mapList(books, BookDto.class);
	}

	@RequestMapping(value = "/api/myborrowedbook", produces = MediaTypes.JSON_UTF_8)
	public List<BookDto> listMyBorrowedBook(HttpSession session) {
		AccountDto currentUser = getCurrentAccount(session);
		List<Book> books = borrowService.listMyBorrowedBook(currentUser.id);
		return BeanMapper.mapList(books, BookDto.class);
	}

	private AccountDto getCurrentAccount(HttpSession session) {
		AccountDto account = (AccountDto) session.getAttribute("account");
		if (account == null) {
			throw new RestException("User doesn't login", HttpStatus.UNAUTHORIZED);
		}
		return account;
	}
}
