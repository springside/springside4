<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="baseUrl" value="http://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}"/>

<html>
<head>
	<title>JMX演示用例</title>
</head>

<body>
	<h2>JMX演示用例</h2>

	<h3>技术说明：</h3>
	<ul>
		<li>演示使用Spring annotation将POJO定义为MBean</li>
		<li>演示使用jolokia将JMX输出为Restul JSON Monitor API</li>
	</ul>

	<h3>MBean介绍:</h3>
	<ul>
		<li>Log4j Mbean，控制Log4j的Logger Level, name为log4j:name=Log4j</li>
		<li>Application Statistics Mbean, 当用户在综合演示里查看/更新用户时，计数器将会递增, name为showcase:name=ApplicationStatistics</li>
	</ul>
	
	<h3>使用Jconsole或其他JMX客户端:</h3>
	<ul>
	<li>如果JConsole与应用在同一台机器，直接选择该进程。</li>
	<li>否则远程进程URL为 localhost:2099 或完整版的service:jmx:rmi:///jndi/rmi://localhost:2099/jmxrmi</li>
	</ul>
	
	<h3>与国际接轨的Resultful API:</h3>
	<ul>
		<li>获取showcase域下的所有MBean的属性: <br/><a href="${baseUrl}/jolokia/read/showcase:name=*">${baseUrl}/jolokia/read/showcase:name=*</a></li>
		<li>获取应用统计MBean下的所有属性: <br/><a href="${baseUrl}/jolokia/read/showcase:name=ApplicationStatistics">${baseUrl}/jolokia/read/showcase:name=ApplicationStatistics</a></li>
		<li>只获取应用统计MBean下的展示用户列表次数属性: <br/><a href="${baseUrl}/jolokia/read/showcase:name=ApplicationStatistics/ListUserTimes">${baseUrl}/jolokia/read/showcase:name=ApplicationStatistics/ListUserTimes</a></li>
		<li>执行重置清零统计信息的命令: <br/><a href="${baseUrl}/jolokia/exec/showcase:name=ApplicationStatistics/resetStatistics">${baseUrl}/jolokia/exec/showcase:name=ApplicationStatistics/resetStatistics</a></li>
		<li>执行获取特定Logger的Level的命令: <br/><a href="${baseUrl}/jolokia/exec/log4j:name=Log4j/getLoggerLevel/org.springframework">${baseUrl}/jolokia/exec/log4j:name=Log4j/getLoggerLevel/org.springframework</a></li>
		<li>列出showcase域下的所有MBean及其描述: <br/><a href="${baseUrl}/jolokia/list/showcase">${baseUrl}/jolokia/list/showcase</a></li>
	</ul>
</body>
</html>