package org.springside.examples.showcase.demos.web;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springside.modules.web.Servlets;

/**
 * 本地静态内容展示与下载的Servlet.
 * 
 * 演示文件高效读取,客户端缓存控制及Gzip压缩传输.
 * 可使用org.springside.examples.showcase.cache包下的Ehcache或本地Map缓存静态内容基本信息(未演示).
 * 
 * 演示访问地址为：
 * static-content?contentPath=static/images/logo.jpg
 * static-content?contentPath=static/images/logo.jpg&download=true
 * 
 * @author calvin
 */
public class StaticContentServlet extends HttpServlet {

	private static final long serialVersionUID = -1855617048198368534L;

	/** 需要被Gzip压缩的Mime类型. */
	private static final String[] GZIP_MIME_TYPES = { "text/html", "application/xhtml+xml", "text/plain", "text/css",
			"text/javascript", "application/x-javascript", "application/json" };

	/** 需要被Gzip压缩的最小文件大小. */
	private static final int GZIP_MINI_LENGTH = 512;

	private MimetypesFileTypeMap mimetypesFileTypeMap;

	private ApplicationContext applicationContext;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 取得参数
		String contentPath = request.getParameter("contentPath");
		if (StringUtils.isBlank(contentPath)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "contentPath parameter is required.");
			return;
		}

		// 获取请求内容的基本信息.
		ContentInfo contentInfo = getContentInfo(contentPath);

		// 根据Etag或ModifiedSince Header判断客户端的缓存文件是否有效, 如仍有效则设置返回码为304,直接返回.
		if (!Servlets.checkIfModifiedSince(request, response, contentInfo.lastModified)
				|| !Servlets.checkIfNoneMatchEtag(request, response, contentInfo.etag)) {
			return;
		}

		// 设置Etag/过期时间
		Servlets.setExpiresHeader(response, Servlets.ONE_YEAR_SECONDS);
		Servlets.setLastModifiedHeader(response, contentInfo.lastModified);
		Servlets.setEtag(response, contentInfo.etag);

		// 设置MIME类型
		response.setContentType(contentInfo.mimeType);

		// 设置弹出下载文件请求窗口的Header
		if (request.getParameter("download") != null) {
			Servlets.setFileDownloadHeader(response, contentInfo.fileName);
		}

		// 构造OutputStream
		OutputStream output;
		if (checkAccetptGzip(request) && contentInfo.needGzip) {
			// 使用压缩传输的outputstream, 使用http1.1 trunked编码不设置content-length.
			output = buildGzipOutputStream(response);
		} else {
			// 使用普通outputstream, 设置content-length.
			response.setContentLength(contentInfo.length);
			output = response.getOutputStream();
		}

		// 高效读取文件内容并输出,然后关闭input file
		FileUtils.copyFile(contentInfo.file, output);
		output.flush();
	}

	/**
	 * 检查浏览器客户端是否支持gzip编码.
	 */
	private static boolean checkAccetptGzip(HttpServletRequest request) {
		// Http1.1 header
		String acceptEncoding = request.getHeader("Accept-Encoding");

		return StringUtils.contains(acceptEncoding, "gzip");
	}

	/**
	 * 设置Gzip Header并返回GZIPOutputStream.
	 */
	private OutputStream buildGzipOutputStream(HttpServletResponse response) throws IOException {
		response.setHeader("Content-Encoding", "gzip");
		response.setHeader("Vary", "Accept-Encoding");
		return new GZIPOutputStream(response.getOutputStream());
	}

	/**
	 * 初始化.
	 */
	@Override
	public void init() throws ServletException {
		// 保存applicationContext以备后用，纯演示.
		applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());

		// 初始化mimeTypes, 默认缺少css的定义,添加之.
		mimetypesFileTypeMap = new MimetypesFileTypeMap();
		mimetypesFileTypeMap.addMimeTypes("text/css css");
	}

	/**
	 * 创建Content基本信息.
	 */
	private ContentInfo getContentInfo(String contentPath) {
		ContentInfo contentInfo = new ContentInfo();

		String realFilePath = getServletContext().getRealPath(contentPath);
		File file = new File(realFilePath);

		contentInfo.file = file;
		contentInfo.contentPath = contentPath;
		contentInfo.fileName = file.getName();
		contentInfo.length = (int) file.length();

		contentInfo.lastModified = file.lastModified();
		contentInfo.etag = "W/\"" + contentInfo.lastModified + "\"";

		contentInfo.mimeType = mimetypesFileTypeMap.getContentType(contentInfo.fileName);

		if ((contentInfo.length >= GZIP_MINI_LENGTH) && ArrayUtils.contains(GZIP_MIME_TYPES, contentInfo.mimeType)) {
			contentInfo.needGzip = true;
		} else {
			contentInfo.needGzip = false;
		}

		return contentInfo;
	}

	/**
	 * 定义Content的基本信息.
	 */
	static class ContentInfo {
		protected String contentPath;
		protected File file;
		protected String fileName;
		protected int length;
		protected String mimeType;
		protected long lastModified;
		protected String etag;
		protected boolean needGzip;
	}
}
