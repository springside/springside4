<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div id="header" class="span-24 last">
	<div id="title">
		<h2>Mini-Web示例</h2>
		<h3>--CRUD管理界面演示</h3>
	</div>
	<div id="menu">
		<ul>
			<li><a href="${ctx}/account/user/">帐号列表</a></li>
			<li><a href="${ctx}/account/group/">权限组列表</a></li>
			<li><a href="${ctx}/logout">退出登录</a></li>
		</ul>
	</div>
</div>