<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<title>Mini-Web 帐号管理</title>
</head>

<body>
	<h4 class="prepend-top">权限组列表</h4>
	<c:if test="${not empty message}">
		<div id="message" class="success">${message}</div>	
	</c:if>
	
	<table>
	<tr><th>名称</th><th>授权</th><th>操作</th></tr>
	<c:forEach items="${groups}" var="group">
		<tr>
			<td>${group.name}</td>
			<td>${group.permissionNames}</td>
			<td>
				<shiro:hasPermission name="group:edit">
					<a href="update/${group.id}">修改</a> <a href="delete/${group.id}">删除</a>
				</shiro:hasPermission>	
			</td>
		</tr>
	</c:forEach>
	</table>
	<shiro:hasPermission name="group:edit">
		<a href="create">新建权限组</a>
	</shiro:hasPermission>
</body>
</html>
