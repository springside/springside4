<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<title>日志高级演示</title>
	<script>
		$(document).ready(function() {
			$("#log-tab").addClass("active");
		});
	</script>
</head>

<body>
	<h1>日志高级演示</h1>

	<h2>用户故事：</h2>
	<ul>
		<li>业务日志，在综合演示中的CRUD操作将产生业务日志，有特殊Time+ Size的滚动规则及格式。</li>
		<li>Logback的JMX控制，见<a href="${ctx}/story/jmx">JMX演示 </a>。</li>
	</ul>
</body>
</html>