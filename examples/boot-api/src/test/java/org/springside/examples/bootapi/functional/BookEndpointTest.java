/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.springside.examples.bootapi.functional;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Map;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springside.examples.bootapi.api.support.ErrorResult;
import org.springside.examples.bootapi.domain.Book;
import org.springside.examples.bootapi.dto.BookDto;
import org.springside.examples.bootapi.repository.BookDao;
import org.springside.examples.bootapi.service.exception.ErrorCode;
import org.springside.modules.mapper.JsonMapper;
import org.springside.modules.test.data.RandomData;

import com.google.common.collect.Maps;

// 测试方法的执行顺序在不同JVM里是不固定的，此处设为按方法名排序，避免方法间数据影响的不确定性
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BookEndpointTest extends BaseFunctionalTest {

	// 注入Spring Context中的BookDao，实现白盒查询数据库实际情况
	@Autowired
	private BookDao bookDao;

	private RestTemplate restTemplate;
	private JsonMapper jsonMapper = new JsonMapper();

	private String resourceUrl;
	private String loginUrl;
	private String logoutUrl;

	@Before
	public void setup() {
		// TestRestTemplate与RestTemplate, 服务端返回非200返回码时，不会抛异常.
		restTemplate = new TestRestTemplate();
		resourceUrl = "http://localhost:" + port + "/api/books";
		loginUrl = "http://localhost:" + port + "/api/accounts/login";
		logoutUrl = "http://localhost:" + port + "/api/accounts/logout";
	}

	@Test
	public void listBook() {
		BookList tasks = restTemplate.getForObject(resourceUrl, BookList.class);
		assertThat(tasks).hasSize(3);
		BookDto firstBook = tasks.get(0);

		assertThat(firstBook.title).isEqualTo("Big Data日知录");
		assertThat(firstBook.owner.name).isEqualTo("Calvin");

		BookDto book = restTemplate.getForObject(resourceUrl + "/{id}", BookDto.class, 1L);
		assertThat(book.title).isEqualTo("Big Data日知录");
		assertThat(book.owner.name).isEqualTo("Calvin");
	}

	@Test
	public void applyRequest() {
		String token = login("calvin.xiao@springside.io");

		ResponseEntity<String> response = restTemplate.getForEntity(resourceUrl + "/{id}/request?token={token}",
				String.class, 3L, token);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// 查询数据库状态
		Book book = bookDao.findOne(3L);
		assertThat(book.borrower.id).isEqualTo(1L);
		assertThat(book.status).isEqualTo(Book.STATUS_REQUEST);

		// 回退操作
		response = restTemplate.getForEntity(resourceUrl + "/{id}/cancel?token={token}", String.class, 3L, token);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		logout(token);
	}

	@Test
	public void applyRequestWithError() {
		// 未设置token
		ResponseEntity<String> response = restTemplate.getForEntity(resourceUrl + "/{id}/request", String.class, 1L);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		ErrorResult errorResult = jsonMapper.fromJson(response.getBody(), ErrorResult.class);
		assertThat(errorResult.code).isEqualTo(ErrorCode.NO_TOKEN.code);

		Book book = bookDao.findOne(1L);
		assertThat(book.borrower).isNull();

		// 设置错误token
		response = restTemplate.getForEntity(resourceUrl + "/{id}/request?token={token}", String.class, 1L, "abc");
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		errorResult = jsonMapper.fromJson(response.getBody(), ErrorResult.class);
		assertThat(errorResult.code).isEqualTo(ErrorCode.UNAUTHORIZED.code);

		book = bookDao.findOne(1L);
		assertThat(book.borrower).isNull();

		// 自己借自己的书
		String token = login("calvin.xiao@springside.io");

		response = restTemplate.getForEntity(resourceUrl + "/{id}/request?token={token}", String.class, 1L, token);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		errorResult = jsonMapper.fromJson(response.getBody(), ErrorResult.class);
		assertThat(errorResult.code).isEqualTo(ErrorCode.BOOK_OWNERSHIP_WRONG.code);

		book = bookDao.findOne(1L);
		assertThat(book.borrower).isNull();

		logout(token);

		// 借一本已被申请借出的书
		token = login("calvin.xiao@springside.io");

		response = restTemplate.getForEntity(resourceUrl + "/{id}/request?token={token}", String.class, 3L, token);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		response = restTemplate.getForEntity(resourceUrl + "/{id}/request?token={token}", String.class, 3L, token);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		errorResult = jsonMapper.fromJson(response.getBody(), ErrorResult.class);
		assertThat(errorResult.code).isEqualTo(ErrorCode.BOOK_STATUS_WRONG.code);

		// 回退操作
		response = restTemplate.getForEntity(resourceUrl + "/{id}/cancel?token={token}", String.class, 3L, token);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		logout(token);
	}

	@Test
	public void fullBorrowProcess() {
		// 发起请求
		String token = login("david.wang@springside.io");

		ResponseEntity<String> response = restTemplate.getForEntity(resourceUrl + "/{id}/request?token={token}",
				String.class, 1L, token);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		logout(token);

		// 确认借出
		token = login("calvin.xiao@springside.io");

		response = restTemplate.getForEntity(resourceUrl + "/{id}/confirm?token={token}", String.class, 1L, token);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// 确认归还
		response = restTemplate.getForEntity(resourceUrl + "/{id}/return?token={token}", String.class, 1L, token);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		logout(token);
	}

	private String login(String user) {
		Map<String, String> map = Maps.newHashMap();
		map.put("email", user);
		map.put("password", "springside");

		ResponseEntity<Map> response = restTemplate.getForEntity(loginUrl + "?email={email}&password={password}",
				Map.class, map);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		return (String) response.getBody().get("token");
	}

	public void logout(String token) {
		restTemplate.getForEntity(logoutUrl + "?token={token}", String.class, token);
	}

	private static BookDto randomBook() {
		BookDto book = new BookDto();
		book.title = RandomData.randomName("Book");

		return book;
	}

	// ArrayList<Task>在RestTemplate转换时不好表示，创建一个类来表达它是最简单的。
	private static class BookList extends ArrayList<BookDto> {
	}

}
