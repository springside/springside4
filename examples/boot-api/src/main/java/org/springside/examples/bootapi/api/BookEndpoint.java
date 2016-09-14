package org.springside.examples.bootapi.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springside.examples.bootapi.domain.Account;
import org.springside.examples.bootapi.domain.Book;
import org.springside.examples.bootapi.dto.BookDto;
import org.springside.examples.bootapi.service.AccountService;
import org.springside.examples.bootapi.service.BookAdminService;
import org.springside.examples.bootapi.service.BookBorrowService;
import org.springside.examples.bootapi.service.exception.ErrorCode;
import org.springside.examples.bootapi.service.exception.ServiceException;
import org.springside.modules.constants.MediaTypes;
import org.springside.modules.mapper.BeanMapper;

// Spring Restful MVC Controller的标识, 直接输出内容，不调用template引擎.
@RestController
public class BookEndpoint {

	private static Logger logger = LoggerFactory.getLogger(BookEndpoint.class);

	@Autowired
	private AccountService accountService;

	@Autowired
	private BookAdminService adminService;

	@Autowired
	private BookBorrowService borrowService;

	@RequestMapping(value = "/api/books", produces = MediaTypes.JSON_UTF_8)
	public List<BookDto> listAllBook(Pageable pageable) {
		Iterable<Book> books = adminService.findAll(pageable);

		return BeanMapper.mapList(books, BookDto.class);
	}

	@RequestMapping(value = "/api/books/{id}", produces = MediaTypes.JSON_UTF_8)
	public BookDto listOneBook(@PathVariable("id") Long id) {
		Book book = adminService.findOne(id);

		return BeanMapper.map(book, BookDto.class);
	}

	@RequestMapping(value = "/api/books", method = RequestMethod.POST, consumes = MediaTypes.JSON_UTF_8)
	public void createBook(@RequestBody BookDto bookDto,
			@RequestParam(value = "token", required = false) String token) {
		checkToken(token);
		// 使用Header中的Token，查找登录用户
		Account currentUser = accountService.getLoginUser(token);

		// 使用BeanMapper, 将与外部交互的BookDto对象复制为应用内部的Book对象
		Book book = BeanMapper.map(bookDto, Book.class);

		// 保存Book对象
		adminService.saveBook(book, currentUser);
	}

	@RequestMapping(value = "/api/books/{id}/modify", method = RequestMethod.POST, consumes = MediaTypes.JSON_UTF_8)
	public void modifyBook(@RequestBody BookDto bookDto,
			@RequestParam(value = "token", required = false) String token) {
		checkToken(token);
		Account currentUser = accountService.getLoginUser(token);
		Book book = BeanMapper.map(bookDto, Book.class);
		adminService.modifyBook(book, currentUser.id);
	}

	@RequestMapping(value = "/api/books/{id}/delete")
	public void deleteBook(@PathVariable("id") Long id, @RequestParam(value = "token", required = false) String token) {
		checkToken(token);
		Account currentUser = accountService.getLoginUser(token);
		adminService.deleteBook(id, currentUser.id);
	}

	@RequestMapping(value = "/api/books/{id}/request")
	public void applyBorrowRequest(@PathVariable("id") Long id,
			@RequestParam(value = "token", required = false) String token) {
		checkToken(token);
		Account currentUser = accountService.getLoginUser(token);
		borrowService.applyBorrowRequest(id, currentUser);
	}

	@RequestMapping(value = "/api/books/{id}/cancel")
	public void cancelBorrowRequest(@PathVariable("id") Long id,
			@RequestParam(value = "token", required = false) String token) {
		checkToken(token);
		Account currentUser = accountService.getLoginUser(token);
		borrowService.cancelBorrowRequest(id, currentUser);
	}

	@RequestMapping(value = "/api/books/{id}/confirm")
	public void markBookBorrowed(@PathVariable("id") Long id,
			@RequestParam(value = "token", required = false) String token) {
		checkToken(token);
		Account currentUser = accountService.getLoginUser(token);
		borrowService.markBookBorrowed(id, currentUser);
	}

	@RequestMapping(value = "/api/books/{id}/reject")
	public void rejectBorrowRequest(@PathVariable("id") Long id,
			@RequestParam(value = "token", required = false) String token) {
		checkToken(token);
		Account currentUser = accountService.getLoginUser(token);
		borrowService.rejectBorrowRequest(id, currentUser);
	}

	@RequestMapping(value = "/api/books/{id}/return")
	public void markBookReturned(@PathVariable("id") Long id,
			@RequestParam(value = "token", required = false) String token) {
		checkToken(token);
		Account currentUser = accountService.getLoginUser(token);
		borrowService.markBookReturned(id, currentUser);
	}

	@RequestMapping(value = "/api/mybook", produces = MediaTypes.JSON_UTF_8)
	public List<BookDto> listMyBook(@RequestParam(value = "token", required = false) String token, Pageable pageable) {
		checkToken(token);
		Account currentUser = accountService.getLoginUser(token);
		List<Book> books = adminService.listMyBook(currentUser.id, pageable);
		return BeanMapper.mapList(books, BookDto.class);
	}

	@RequestMapping(value = "/api/myborrowedbook", produces = MediaTypes.JSON_UTF_8)
	public List<BookDto> listMyBorrowedBook(@RequestParam(value = "token", required = false) String token,
			Pageable pageable) {
		checkToken(token);
		Account currentUser = accountService.getLoginUser(token);
		List<Book> books = borrowService.listMyBorrowedBook(currentUser.id, pageable);
		return BeanMapper.mapList(books, BookDto.class);
	}

	private void checkToken(String token) {
		if (token == null) {
			throw new ServiceException("No token in request", ErrorCode.NO_TOKEN);
		}
	}
}
