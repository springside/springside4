<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<html>
<head>
	<title>跨域名Mashup演示</title>
	<script type="text/javascript">
		//相对于本地文件来说，服务器地址就是跨域的地址.
		var remoteUrl = "http://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}";

		//使用JQuery ajax jsonp方式跨域名访问内容
		function fetchMashupContent() {
		$.ajax({
				url:remoteUrl + "/web/mashup", 
				dataType:"jsonp",
				success:function(data) {
					$('#mashupContent').html(data.content);
					$('#mashupContent').show();
			}});
		}
	</script>
</head>

<body>
	<h2>Mashup演示</h2>

	<h3>技术说明：</h3>
	<p>
		演示基于JQuery的JSONP实现，绕过浏览器对Ajax不能访问跨域名网站内容的限制实现Mashup.<br /> 请先将本页另存为本地html文件，打开本地文件访问本地应用服务器中的内容即为跨域访问的场景.
	</p>
	<p>
		<input type="button" class="btn" value="获取内容" onclick="fetchMashupContent();" />
	</p>
	<p>跨域页面内容:</p>
	<div id="mashupContent" style="display: none"></div>
</body>
</html>