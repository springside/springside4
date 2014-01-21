/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.examples.showcase.demos.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 演示使用多线程安全且带连接池的Apache HttpClient和 JDK两种方案获取远程静态内容并进行展示的Servlet.
 * 
 * 另外简单演示了轻量级更易用的Apache HttpClient Fluent API。
 * 
 * 演示访问地址如下(contentUrl已经过URL编码):
 * remote-content?contentUrl=http%3A%2F%2Flocalhost%3A8080%2Fshowcase%2Fimages%2Flogo.jpg
 * 
 * @author calvin
 */
public class RemoteContentServlet extends HttpServlet {

	private static final long serialVersionUID = -8483811141908827663L;

	private static final int TIMEOUT_SECONDS = 20;

	private static final int POOL_SIZE = 20;

	private static Logger logger = LoggerFactory.getLogger(RemoteContentServlet.class);

	private static CloseableHttpClient httpClient;

	@Override
	public void init(ServletConfig config) throws ServletException {
		initApacheHttpClient();
	}

	@Override
	public void destroy() {
		destroyApacheHttpClient();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 获取URL
		String contentUrl = request.getParameter("contentUrl");
		if (StringUtils.isBlank(contentUrl)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "contentUrl parameter is required.");
		}

		// 基於配置，使用HttpClient或JDK 獲取URL內容
		String client = request.getParameter("client");
		if ("apache".equals(client)) {
			fetchContentByApacheHttpClient(response, contentUrl);
		} else {
			fetchContentByJDKConnection(response, contentUrl);
		}
	}

	private void fetchContentByApacheHttpClient(HttpServletResponse response, String contentUrl) throws IOException {
		// 获取内容
		HttpGet httpGet = new HttpGet(contentUrl);
		CloseableHttpResponse remoteResponse = httpClient.execute(httpGet);
		try {
			// 判断返回值
			int statusCode = remoteResponse.getStatusLine().getStatusCode();
			if (statusCode >= 400) {
				response.sendError(statusCode, "fetch image error from " + contentUrl);
				return;
			}

			HttpEntity entity = remoteResponse.getEntity();

			// 设置Header
			response.setContentType(entity.getContentType().getValue());
			if (entity.getContentLength() > 0) {
				response.setContentLength((int) entity.getContentLength());
			}
			// 输出内容
			InputStream input = entity.getContent();
			OutputStream output = response.getOutputStream();
			// 基于byte数组读取InputStream并直接写入OutputStream, 数组默认大小为4k.
			IOUtils.copy(input, output);
			output.flush();
		} finally {
			remoteResponse.close();
		}
	}

	private void fetchContentByJDKConnection(HttpServletResponse response, String contentUrl) throws IOException {

		HttpURLConnection connection = (HttpURLConnection) new URL(contentUrl).openConnection();
		// 设置Socket超时
		connection.setReadTimeout(TIMEOUT_SECONDS * 1000);
		try {
			connection.connect();

			// 真正发出请求
			InputStream input;
			try {
				input = connection.getInputStream();
			} catch (FileNotFoundException e) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, contentUrl + " is not found.");
				return;
			}

			// 设置Header
			response.setContentType(connection.getContentType());
			if (connection.getContentLength() > 0) {
				response.setContentLength(connection.getContentLength());
			}

			// 输出内容
			OutputStream output = response.getOutputStream();
			try {
				// 基于byte数组读取InputStream并直接写入OutputStream, 数组默认大小为4k.
				IOUtils.copy(input, output);
				output.flush();
			} finally {
				// 保证InputStream的关闭.
				IOUtils.closeQuietly(input);
			}
		} finally {
			connection.disconnect();
		}
	}

	// 创建包含connection pool与超时设置的client
	private void initApacheHttpClient() {
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(TIMEOUT_SECONDS * 1000)
				.setConnectTimeout(TIMEOUT_SECONDS * 1000).build();

		httpClient = HttpClientBuilder.create().setMaxConnTotal(POOL_SIZE).setMaxConnPerRoute(POOL_SIZE)
				.setDefaultRequestConfig(requestConfig).build();
	}

	private void destroyApacheHttpClient() {
		try {
			httpClient.close();
		} catch (IOException e) {
			logger.error("httpclient close fail", e);
		}
	}

	/**
	 * 演示FluentAPI。
	 */
	@SuppressWarnings("unused")
	public void fluentAPIDemo(String contentUrl) throws IOException {
		try {
			// demo1: 获取文字 , 使用默认连接池(200 total/100 per route), returnContent()会自动获取全部内容后关闭inputstream。
			String resultString = Request.Get(contentUrl).execute().returnContent().asString();

			// demo2: 获取图片, 增加超时设定。
			byte[] resultBytes = Request.Get(contentUrl).connectTimeout(TIMEOUT_SECONDS * 1000)
					.socketTimeout(TIMEOUT_SECONDS * 1000).execute().returnContent().asBytes();

			// demo3: 获取图片，使用之前设置好了的自定义连接池与超时的httpClient
			Executor executor = Executor.newInstance(httpClient);
			String resultString2 = executor.execute(Request.Get(contentUrl)).returnContent().asString();
		} catch (HttpResponseException e) {
			logger.error("Status code:" + e.getStatusCode(), e);
		}
	}
}
