package org.springside.examples.showcase.demos.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取远程静态内容并进行展示的Servlet.
 * 
 * 演示使用多线程安全的Apache HttpClient获取远程静态内容.
 * 
 * 演示访问地址如下(contentUrl已经过URL编码):
 * remote-content?contentUrl=http%3A%2F%2Flocalhost%3A8080%2Fshowcase%2Fimages%2Flogo.jpg
 * 
 * @author calvin
 */
public class RemoteContentServlet extends HttpServlet {

	private static final long serialVersionUID = -8483811141908827663L;

	private static final int CONNECTION_POOL_SIZE = 10;
	private static final int TIMEOUT_SECONDS = 20;

	private static Logger logger = LoggerFactory.getLogger(RemoteContentServlet.class);

	private HttpClient httpClient = null;

	/**
	 * 创建多线程安全的HttpClient实例.
	 */
	@Override
	public void init() throws ServletException {
		// Set connection pool
		PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
		cm.setMaxTotal(CONNECTION_POOL_SIZE);
		httpClient = new DefaultHttpClient(cm);

		// set timeout
		HttpParams httpParams = httpClient.getParams();
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_SECONDS * 1000);
	}

	/**
	 * 销毁HttpClient实例.
	 */
	@Override
	public void destroy() {
		if (httpClient != null) {
			httpClient.getConnectionManager().shutdown();
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 获取参数
		String contentUrl = request.getParameter("contentUrl");
		if (StringUtils.isBlank(contentUrl)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "contentUrl parameter is required.");
		}

		// 远程访问获取内容的方式
		String client = request.getParameter("client");

		InputStream input = null;
		if ("apache".equals(client)) {
			// 使用Apache HttpClient
			fetchContentByApacheHttpClient(response, contentUrl);
		} else {
			// 使用JDK HttpUrlConnection
			fetchContentByJDKConnection(response, contentUrl);
		}
	}

	/**
	 * 使用HttpClient取得内容.
	 */
	private void fetchContentByApacheHttpClient(HttpServletResponse response, String contentUrl) throws IOException {

		// 获取内容
		HttpEntity entity = null;
		HttpGet httpGet = new HttpGet(contentUrl);
		try {
			HttpContext context = new BasicHttpContext();
			HttpResponse remoteResponse = httpClient.execute(httpGet, context);
			entity = remoteResponse.getEntity();
		} catch (Exception e) {
			logger.error("fetch remote content" + contentUrl + "  error", e);
			httpGet.abort();
			return;
		}

		// 404返回
		if (entity == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, contentUrl + " is not found.");
			return;
		}

		// 设置Header
		response.setContentType(entity.getContentType().getValue());
		if (entity.getContentLength() > 0) {
			response.setContentLength((int) entity.getContentLength());
		}

		// 输出内容
		InputStream input = entity.getContent();
		OutputStream output = response.getOutputStream();
		try {
			// 基于byte数组读取InputStream并直接写入OutputStream, 数组默认大小为4k.
			IOUtils.copy(input, output);
			output.flush();
		} finally {
			// 保证InputStream的关闭.
			IOUtils.closeQuietly(input);
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
}
