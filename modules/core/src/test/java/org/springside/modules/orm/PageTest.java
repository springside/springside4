package org.springside.modules.orm;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

public class PageTest {
	private Page<Object> page;
	private PageRequest request;

	@Before
	public void setUp() {
		request = new PageRequest();
		page = new Page<Object>(request);
	}

	@Test
	public void getTotalPages() {

		page.setTotalItems(1);
		assertEquals(1, page.getTotalPages());

		page.setTotalItems(10);
		assertEquals(1, page.getTotalPages());

		page.setTotalItems(11);
		assertEquals(2, page.getTotalPages());
	}

	@Test
	public void hasNextOrPre() {

		page.setTotalItems(9);
		assertEquals(false, page.hasNextPage());

		page.setTotalItems(11);
		assertEquals(true, page.hasNextPage());

		page.setPageNo(1);
		assertEquals(false, page.hasPrePage());

		page.setPageNo(2);
		assertEquals(true, page.hasPrePage());
	}

	@Test
	public void getNextOrPrePage() {
		request.setPageNo(1);
		assertEquals(1, page.getPrePage());

		request.setPageNo(2);
		assertEquals(1, page.getPrePage());

		page.setTotalItems(11);
		request.setPageNo(1);
		assertEquals(2, page.getNextPage());

		request.setPageNo(2);
		assertEquals(2, page.getNextPage());
	}
}
