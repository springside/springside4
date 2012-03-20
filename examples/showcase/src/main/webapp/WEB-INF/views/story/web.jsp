<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.springside.modules.utils.Encodes" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<%
   String remoteImageUrl = "http://"+request.getServerName()+":"+request.getServerPort()+"/showcase/static/img/logo.jpg";
   String encodedImageUrl = Encodes.urlEncode(remoteImageUrl);
%>
<html>
<head>
	<title>Web高级演示</title>
</head>
<body>
		<h2>Web高级演示</h2>

		<h4>高性能Web2.0网站</h4>
		<p>
			    1. 静态内容Servlet, 演示高效读取静态内容, 控制客户端缓存, 压缩传输, 弹出下载对话框.<br/>
			       <img src="${ctx}/static-content?contentPath=static/img/logo.jpg"/> <a href="${ctx}/static-content?contentPath=img/logo.jpg&download=true">图片下载链接</a><br/>
			 	2. 远程内容Servlet, 演示使用Apache HttpClient多线程高效获取远程网站内容.<br/>
			 	   <img src="${ctx}/remote-content?contentUrl=<%=encodedImageUrl%>"/><br/>
			    3. CacheControlHeaderFilter为静态内容添加缓存控制 Header,见web.xml<br/>
			    4. YUI Compressor 压缩js/css,见bin/yuicompressor.bat命令及webapp中两个版本的js/css文件<br/>
		</p>
		<h4>跨域名Mashup演示</h4>
		<p>
			<a href="${ctx}/web/mashup-client">跨域名Mashup演示</a> 演示基于JSONP Mashup 跨域名网站的内容.
		</p>
</body>
</html>