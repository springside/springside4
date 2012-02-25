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

		<h4>技术说明：</h4>
		<ul>
			<li>高性能Web2.0网站:<br/>
			    1. 静态内容Servlet, 演示高效读取静态内容, 控制客户端缓存, 压缩传输, 弹出下载对话框.<br/>
			 	2. 远程内容Servlet, 演示使用Apache HttpClient多线程高效获取远程网站内容.<br/>
			    3. CacheControlHeaderFilter为静态内容添加缓存控制 Header<br/>
			    4. YUI Compressor 压缩js/css<br/>
			</li>
			<li><a href="${ctx}/web/mashup-client">跨域名Mashup演示</a> 演示基于JSONP Mashup 跨域名网站的内容.</li>
		</ul>
		
		<h4>用户故事：</h4>
		<ul>
			<li>静态内容Servlet:<img src="${ctx}/static-content?contentPath=static/img/logo.jpg"/> <a href="${ctx}/static-content?contentPath=img/logo.jpg&download=true">图片下载链接</a></li>
			<li>远程内容Servlet:<img src="${ctx}/mashup-content?contentUrl=<%=encodedImageUrl%>"/></li>
			<li>CacheControlHeaderFilter使用见webapp中的web.xml</li>
			<li>YUI Compressor见bin/yuicompressor.bat命令及webapp中两个版本的js/css文件.</li>
		</ul>
</body>
</html>