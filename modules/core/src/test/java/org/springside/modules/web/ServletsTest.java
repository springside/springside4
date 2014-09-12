/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.web;

import static org.assertj.core.api.Assertions.*;

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
		assertThat(Servlets.checkIfModifiedSince(request, response, (new Date().getTime() - 2000))).isTrue();

		// 设置If-Modified-Since Header
		request.addHeader("If-Modified-Since", new Date().getTime());
		// 文件修改时间比Header时间小,文件未修改, 返回false.
		assertThat(Servlets.checkIfModifiedSince(request, response, (new Date().getTime() - 2000))).isFalse();
		// 文件修改时间比Header时间大,文件已修改, 返回true,需要传输内容.
		assertThat(Servlets.checkIfModifiedSince(request, response, (new Date().getTime() + 2000))).isTrue();
	}

	@Test
	public void checkIfNoneMatch() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		// 未设Header,返回true,需要传输内容
		assertThat(Servlets.checkIfNoneMatchEtag(request, response, "V1.0")).isTrue();

		// 设置If-None-Match Header
		request.addHeader("If-None-Match", "V1.0,V1.1");
		// 存在Etag
		assertThat(Servlets.checkIfNoneMatchEtag(request, response, "V1.0")).isFalse();
		// 不存在Etag
		assertThat(Servlets.checkIfNoneMatchEtag(request, response, "V2.0")).isTrue();
	}

	@Test
	public void getParametersStartingWith() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("pre_a", "aa");
		request.addParameter("pre_b", "bb");
		request.addParameter("c", "c");
		Map<String, Object> result = Servlets.getParametersStartingWith(request, "pre_");
		assertThat(result).containsOnly(entry("a", "aa"), entry("b", "bb"));

		result = Servlets.getParametersStartingWith(request, "error_");
		assertThat(result).isEmpty();

		result = Servlets.getParametersStartingWith(request, null);
		assertThat(result).hasSize(3);
	}

	@Test
	public void encodeParameterStringWithPrefix() {
		Map<String, Object> params = Maps.newLinkedHashMap();
		params.put("name", "foo");
		params.put("age", "1");

		String queryString = Servlets.encodeParameterStringWithPrefix(params, "search_");
		assertThat(queryString).isEqualTo("search_name=foo&search_age=1");

		// data type is not String
		params.clear();
		params.put("name", "foo");
		params.put("age", 1);
		queryString = Servlets.encodeParameterStringWithPrefix(params, "search_");
		assertThat(queryString).isEqualTo("search_name=foo&search_age=1");

		// prefix is blank or null
		queryString = Servlets.encodeParameterStringWithPrefix(params, null);
		assertThat(queryString).isEqualTo("name=foo&age=1");

		queryString = Servlets.encodeParameterStringWithPrefix(params, "");
		assertThat(queryString).isEqualTo("name=foo&age=1");

		// map is empty or null
		queryString = Servlets.encodeParameterStringWithPrefix(null, "search_");
		assertThat(queryString).isEmpty();

		params.clear();
		queryString = Servlets.encodeParameterStringWithPrefix(params, "search_");
		assertThat(queryString).isEmpty();
	}
}
