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

package org.springside.examples.bootservice.functional;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import org.springside.examples.bootapi.BootApiApplication;
import org.springside.examples.bootapi.dto.BookDto;
import org.springside.modules.test.data.RandomData;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BootApiApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
@DirtiesContext
public class BookEndpointTest {

	@Value("${local.server.port}")
	private int port;

	private RestTemplate restTemplate;
	private String resourceUrl;

	@Before
	public void setup() {
		restTemplate = new TestRestTemplate();
		resourceUrl = "http://localhost:" + port + "/api/books";
	}

	@Test
	public void listBook() {
		BookList tasks = restTemplate.getForObject(resourceUrl, BookList.class);
		assertThat(tasks).hasSize(3);
		BookDto firstBook = tasks.get(0);

		assertThat(firstBook.title).isEqualTo("Big Data日知录");
		assertThat(firstBook.owner.name).isEqualTo("Calvin");
	}

	@Test
	public void getTask() {
		BookDto book = restTemplate.getForObject(resourceUrl + "/{id}", BookDto.class, 1L);
		assertThat(book.title).isEqualTo("Big Data日知录");
		assertThat(book.owner.name).isEqualTo("Calvin");
	}

	public static BookDto randomBook() {
		BookDto book = new BookDto();
		book.title = RandomData.randomName("Book");

		return book;
	}

	// ArrayList<Task>在RestTemplate转换时不好表示，创建一个类来表达它是最简单的。
	private static class BookList extends ArrayList<BookDto> {
	}

}
