<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="baseUrl" value="http://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}"/>

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

	<h2>业务日志</h2>
	<ul>
		<li>业务日志，在综合演示中的CRUD操作将产生业务日志。</li>
		<li>有特殊Time+ Size的滚动规则, 格式为规定字段(时间，entity类型，动作，操作用户)+JSON的扩展字段。</li>
	</ul>
	
	<h2>Logback的JMX控制</h2>
	<ul>
		<li>获取org.springframework Logger的Level: <br/><a href="${baseUrl}/jolokia/exec/ch.qos.logback.classic:Name=default,Type=ch.qos.logback.classic.jmx.JMXConfigurator/getLoggerEffectiveLevel/org.springframework">JMXConfigurator.getLoggerEffectiveLevel("org.springframework")</a></li>
		<li>设置org.springframework Logger的Level: <br/><a href="${baseUrl}/jolokia/exec/ch.qos.logback.classic:Name=default,Type=ch.qos.logback.classic.jmx.JMXConfigurator/setLoggerLevel/org.springframework/ERROR">JMXConfigurator.setLoggerLevel("org.springframework","ERROR")</a></li>
	</ul>
	
</body>
</html>