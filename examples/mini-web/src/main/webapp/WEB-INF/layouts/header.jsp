<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div id="header" class="span-24 last">
	<div id="title">
		<div class="title">Mini-Web示例</div>
		<div>
		<span class="subtitle">--CRUD管理界面演示</span>
		<shiro:user>
			<span class="right">Hello, <shiro:principal property="name"/>!!</span>
		</shiro:user>
		</div>
	</div>
	<div id="menu">
		<ul>
			<shiro:user>
			<shiro:hasPermission name="user:view">
				<li><a href="${ctx}/account/user/">帐号列表</a></li>
			</shiro:hasPermission>
			<shiro:hasPermission name="group:view">
				<li><a href="${ctx}/account/group/">权限组列表</a></li>
			</shiro:hasPermission>
			<li><a href="${ctx}/logout">退出登录</a></li>
			</shiro:user>
			<shiro:guest>
				<li><a href="${ctx}/login">登录</a></li>
			</shiro:guest>
		</ul>
		
	</div>
</div>