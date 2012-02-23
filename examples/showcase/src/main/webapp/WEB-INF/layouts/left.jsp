<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div id="leftbar" class="span-6">
		<a href="${ctx}/">首页</a>
		<a href="${ctx}/common/user/">综合演示</a>
		<a href="${ctx}/story/jms/index">JMS演示</a>
		<a href="${ctx}/story/cache/index">Cache演示</a>
		<a href="${ctx}/story/schedule/index">定时任务演示</a>
		<a href="${ctx}/story/security/index">安全演示</a>
		<a href="${ctx}/story/web/index">Web演示</a>
		<a href="${ctx}/story/webservice/index">WebService演示</a>
		<a href="${ctx}/story/jmx/index">JMX演示</a>
		<a href="${ctx}/story/utilizes/index">Utilizes演示</a>
		<a href="${ctx}/security/login!logout.action">退出登录</a>
</div>