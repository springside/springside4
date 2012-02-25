<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div id="leftbar" class="span-6">
		<a href="${ctx}/">首页</a>
		<a href="${ctx}/common/user/">综合演示</a>
		<a href="${ctx}/story/jms/">JMS演示</a>
		<a href="${ctx}/story/cache/">Cache演示</a>
		<a href="${ctx}/story/schedule/">定时任务演示</a>
		<a href="${ctx}/story/security/">安全演示</a>
		<a href="${ctx}/story/web/">Web演示</a>
		<a href="${ctx}/story/webservice/">WebService演示</a>
		<a href="${ctx}/story/jmx/">JMX演示</a>
		<a href="${ctx}/story/utilizes/">Utilizes演示</a>
		<a href="${ctx}/security/login!logout.action">退出登录</a>
</div>