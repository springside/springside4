<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div id="leftbar" class="span2">
	<h1>综合演示</h1>
	<div class="submenu">
		<a id="account-tab"href="${ctx}/account/user/">帐号管理</a>
	</div>
	<h1>独立演示</h1>
	<div class="submenu">
		<a id="web-tab" href="${ctx}/story/web">Web演示</a>
		<a id="webservice-tab"href="${ctx}/story/webservice">WebService演示</a>
		<a id="jmx-tab" href="${ctx}/story/jmx">JMX演示</a>
	</div>
	<h1>演示说明</h1>
	<div class="submenu">
		<a id="persistence-tab" href="${ctx}/story/persistence">持久化高级演示</a>
		<a id="jms-tab" href="${ctx}/story/jms">JMS演示</a>
		<a id="reids-tab" href="${ctx}/story/redis">Redis演示</a>
		<a id="cache-tab" href="${ctx}/story/cache">Cache演示</a>
		<a id="schedule-tab" href="${ctx}/story/schedule">定时任务演示</a>
		<a id="security-tab" href="${ctx}/story/security">安全高级演示</a>
		<a id="log-tab" href="${ctx}/story/log">日志高级演示</a>
		<a id="utilizes-tab"href="${ctx}/story/utilizes">工具类演示</a>
		<a id="executablewar-tab"href="${ctx}/story/executablewar">可运行war包演示</a>
	</div>
</div>