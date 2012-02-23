package org.springside.modules.orm;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springside.modules.orm.PageRequest;

public class PageRequestTest {
	private PageRequest pageRequest;

	@Before
	public void setUp() {
		pageRequest = new PageRequest();
	}

	/**
	 * 检测Page的默认值契约.
	 */
	@Test
	public void defaultParameter() {
		assertEquals(1, pageRequest.getPageNo());
		assertEquals(10, pageRequest.getPageSize());

		pageRequest.setPageNo(-1);
		assertEquals(1, pageRequest.getPageNo());

		assertNull(pageRequest.getOrderDir());
		assertNull(pageRequest.getOrderBy());
		assertEquals(false, pageRequest.isOrderBySetted());

		pageRequest.setOrderBy("Id");
		assertEquals(false, pageRequest.isOrderBySetted());
		pageRequest.setOrderDir("ASC,desc");
		assertEquals(true, pageRequest.isOrderBySetted());
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkInvalidOrderDirParameter() {
		pageRequest.setOrderDir("asc,abcd");
	}

	@Test
	public void offset() {
		pageRequest.setPageNo(2);
		assertEquals(10, pageRequest.getOffset());
	}

}
