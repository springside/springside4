<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.springside.examples.showcase.demos.hystrix.web.HystrixController" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%!  %>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<title>Web Service高级演示</title>
	<script>
		$(document).ready(function() {
			$("#hystrix-tab").addClass("active");
		});
	</script>
</head>

<body>
	<h1>Hystrix 演示</h1>

	<h2>当前资源状态为<%= HystrixController.status %></h2>
	<ul>
		<li><a href="${ctx}/story/hystrix/timeout">切换为Timeout</a></li>
		<li><a href="${ctx}/story/hystrix/fail">切换为Fail</a></li>
		<li><a href="${ctx}/story/hystrix/normal">切换为Normal</a></li>
	</ul>

</body>
</html>