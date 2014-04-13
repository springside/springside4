<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="baseUrl" value="http://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}"/>

<html>
<head>
	<title>JMX演示用例</title>
	<script>
		$(document).ready(function() {
			$("#jmx-tab").addClass("active");
		});
	</script>
</head>

<body>
	<h1>JMX演示用例</h1>

	<h2>技术说明</h2>
	<ul>
		<li>演示使用Spring annotation将POJO定义为MBean</li>
		<li>演示使用jolokia将JMX输出为Restul JSON Monitor API</li>
	</ul>

	<h2>MBean介绍</h2>
	<ul>
		<li>Application Statistics Mbean, 当用户在综合演示里查看/更新用户时，计数器将会递增, name为showcase:name=ApplicationStatistics</li>
		<li>Tomcat JDBC 和  Logback同样提供JMX支持</li>
	</ul>
	
	<h2>使用Jconsole或其他JMX客户端</h2>
	<ul>
	<li>如果JConsole与应用在同一台机器，直接选择该进程。</li>
	<li>否则远程进程URL为 localhost:2099 或完整版的service:jmx:rmi:///jndi/rmi://localhost:2099/jmxrmi</li>
	</ul>
	
	<hr/>
	<h2>与国际接轨的JMX Resultful API</h2>
	<p>以下种种，成本就是在web.xml里添加一个jolokia的servlet而已。</p>
	查询：
	<ul>
		<li>列出showcase域下的所有MBean及其描述: <br/><a href="${baseUrl}/jolokia/list/showcase">/jolokia/list/showcase</a></li>
		<li>获取showcase域下的所有MBean的属性: <br/><a href="${baseUrl}/jolokia/read/showcase:name=*">/jolokia/read/showcase:name=*</a></li>
		<li>获取应用统计MBean的所有属性: <br/><a href="${baseUrl}/jolokia/read/showcase:name=ApplicationStatistics">/jolokia/read/showcase:name=ApplicationStatistics</a></li>
		<li>只获取应用统计MBean的展示用户列表次数属性: <br/><a href="${baseUrl}/jolokia/read/showcase:name=ApplicationStatistics/ListUserTimes">/jolokia/read/showcase:name=ApplicationStatistics/ListUserTimes</a></li>
	</ul>
	设置：
	<ul>
		<li>设置jolokia的属性: <br/><a href="${baseUrl}/jolokia/write/jolokia:type=Config/HistoryMaxEntries/20">/jolokia/write/jolokia:type=Config/HistoryMaxEntries/20</a></li>
	</ul>
	
	执行：
	<ul>
		<li>执行重置清零统计信息的命令: <br/><a href="${baseUrl}/jolokia/exec/showcase:name=ApplicationStatistics/resetStatistics">/jolokia/exec/showcase:name=ApplicationStatistics/resetStatistics</a></li>
		<li>执行获取特定Logger的Level的命令: <br/><a href="${baseUrl}/jolokia/exec/ch.qos.logback.classic:Name=default,Type=ch.qos.logback.classic.jmx.JMXConfigurator/getLoggerEffectiveLevel/org.springframework">/jolokia/exec/ch.qos.logback.classic:Name=default,Type=ch.qos.logback.classic.jmx.JMXConfigurator/getLoggerEffectiveLevel/org.springframework</a></li>
	</ul>
</body>
</html>