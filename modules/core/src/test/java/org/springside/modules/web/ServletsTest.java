package org.springside.modules.web;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Map;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.common.collect.Maps;

public class ServletsTest {

	@Test
	public void checkIfModified() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		// 未设Header,返回true,需要传输内容
		assertEquals(true, Servlets.checkIfModifiedSince(request, response, (new Date().getTime() - 2000)));

		// 设置If-Modified-Since Header
		request.addHeader("If-Modified-Since", new Date().getTime());
		// 文件修改时间比Header时间小,文件未修改, 返回false.
		assertEquals(false, Servlets.checkIfModifiedSince(request, response, (new Date().getTime() - 2000)));
		// 文件修改时间比Header时间大,文件已修改, 返回true,需要传输内容.
		assertEquals(true, Servlets.checkIfModifiedSince(request, response, (new Date().getTime() + 2000)));
	}

	@Test
	public void checkIfNoneMatch() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		// 未设Header,返回true,需要传输内容
		assertEquals(true, Servlets.checkIfNoneMatchEtag(request, response, "V1.0"));

		// 设置If-None-Match Header
		request.addHeader("If-None-Match", "V1.0,V1.1");
		// 存在Etag
		assertEquals(false, Servlets.checkIfNoneMatchEtag(request, response, "V1.0"));
		// 不存在Etag
		assertEquals(true, Servlets.checkIfNoneMatchEtag(request, response, "V2.0"));
	}

	@Test
	public void getParametersStartingWith() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("pre_a", "aa");
		request.addParameter("pre_b", "bb");
		request.addParameter("c", "c");
		Map<String, Object> result = Servlets.getParametersStartingWith(request, "pre_");
		assertEquals(2, result.size());
		assertTrue(result.keySet().contains("a"));
		assertTrue(result.keySet().contains("b"));
		assertTrue(result.values().contains("aa"));
		assertTrue(result.values().contains("bb"));

		result = Servlets.getParametersStartingWith(request, "error_");
		assertEquals(0, result.size());

		result = Servlets.getParametersStartingWith(request, null);
		assertEquals(3, result.size());
	}

	@Test
	public void encodeParameterStringWithPrefix() {
		Map<String, Object> params = Maps.newLinkedHashMap();
		params.put("name", "foo");
		params.put("age", "1");

		String queryString = Servlets.encodeParameterStringWithPrefix(params, "search_");
		assertEquals("search_name=foo&search_age=1", queryString);

		// data type is not String
		params.clear();
		params.put("name", "foo");
		params.put("age", 1);
		queryString = Servlets.encodeParameterStringWithPrefix(params, "search_");
		assertEquals("search_name=foo&search_age=1", queryString);

		// prefix is blank or null
		queryString = Servlets.encodeParameterStringWithPrefix(params, null);
		assertEquals("name=foo&age=1", queryString);

		queryString = Servlets.encodeParameterStringWithPrefix(params, "");
		assertEquals("name=foo&age=1", queryString);

		// map is empty or null
		queryString = Servlets.encodeParameterStringWithPrefix(null, "search_");
		assertEquals("", queryString);

		params.clear();
		queryString = Servlets.encodeParameterStringWithPrefix(params, "search_");
		assertEquals("", queryString);
	}
}
