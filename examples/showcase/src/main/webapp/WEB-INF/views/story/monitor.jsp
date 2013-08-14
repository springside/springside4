<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<html>
<head>
	<title>应用监控演示</title>
	<script>
		$(document).ready(function() {
			$("#monitor-tab").addClass("active");
		});
	</script>
</head>

<body>
	<h1>应用监控演示</h1>

	<h2>用户故事</h2>
	<ul>
		<li>监控AccountService类及两个相关类</li>
		<li>点击综合演示页面，修改并保存用户，或者运行JMeter压力测试脚本(support/jmeter/showcase-gui.jmx)</li>
		<li>要监控数据库访问的性能，打开javasimon jdbc driver的注释</li>
	</ul>
	
	<h2>JavaSimon DashBoard</h2>
	<ul>
		<li><a href="${ctx}/javasimon" target="_blank">${ctx}/javasimon</a></li>
	</ul>
	
</body>
</html>