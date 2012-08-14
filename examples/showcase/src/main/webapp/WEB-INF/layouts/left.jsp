<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div id="leftbar" class="span2">
	<h1>综合演示</h1>
	<div class="submenu">
    <ul>
   		<li id="account-tab"><a href="${ctx}/account/user/">帐号管理</a></li>
   		<shiro:guest>
			<li><a href="${ctx}/login">登录</a></li>
		</shiro:guest>
		<shiro:user>
			<li><a href="${ctx}/logout">退出登录</a></li>
		</shiro:user>
	</ul>
	</div>
	<h1>独立演示</h1>
	<div class="submenu">
	<ul>
		<li id="web-tab"><a href="${ctx}/story/web">Web演示</a></li>
		<li id="log-tab"><a href="${ctx}/log/console">日志控制演示</a></li>
		<li id="jmx-tab"><a href="${ctx}/story/jmx">JMX演示</a></li>
	</ul>
	</div>
	<h1>演示说明</h1>
	<div class="submenu">
	<ul>
		<li id="persistence-tab"><a href="${ctx}/story/persistence">持久化高级演示</a></li>
		<li id="webservice-tab"><a href="${ctx}/story/webservice">WebService演示</a></li>
		<li id="jms-tab"><a href="${ctx}/story/jms">JMS演示</a></li>
		<li id="cache-tab"><a href="${ctx}/story/cache">Cache演示</a></li>
		<li id="schedule-tab"><a href="${ctx}/story/schedule">定时任务演示</a></li>
		<li id="security-tab"><a href="${ctx}/story/security">安全高级演示</a></li>
		<li id="utilizes-tab"><a href="${ctx}/story/utilizes">工具类演示</a></li>
		<li id="executablewar-tab"><a href="${ctx}/story/executablewar">可运行war包演示</a></li>
	</ul>
	</div>
</div>