package org.springside.examples.showcase.demos.web;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class CacheControlHeaderFilterTest {
	@Test
	public void test() throws IOException, ServletException {
		MockFilterConfig config = new MockFilterConfig();
		MockFilterChain chain = new MockFilterChain();
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		config.addInitParameter("expiresSeconds", "123");

		CacheControlHeaderFilter filter = new CacheControlHeaderFilter();
		filter.init(config);
		filter.doFilter(request, response, chain);

		assertEquals("private, max-age=123", response.getHeader("Cache-Control"));
	}
}
