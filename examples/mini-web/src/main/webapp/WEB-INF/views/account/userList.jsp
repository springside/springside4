<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<title>帐号管理</title>
	<script>
		$(document).ready(function() {
			//聚焦第一个输入框
			$("#user-tab").addClass("active");
		});
	</script>
</head>

<body>
	<h4>用户列表</h4>
	<c:if test="${not empty message}">
		<div id="message" class="alert alert-success"><button data-dismiss="alert" class="close">×</button>${message}</div>
	</c:if>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>登录名</th><th>用户名</th><th>邮箱</th><th>权限组<th>操作</th></tr></thead>
		<tbody>
		<c:forEach items="${users}" var="user">
			<tr>
				<td>${user.loginName}</td>
				<td>${user.name}</td>
				<td>${user.email}</td>
				<td>${user.groupNames}</td>
				<td>
					<shiro:hasPermission name="user:edit">
	    				<a href="update/${user.id}" id="editLink-${user.name}">修改</a> <a href="delete/${user.id}">删除</a>
					</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<shiro:hasPermission name="user:edit">
		<a class="btn" href="create">创建用户</a>
	</shiro:hasPermission>
</body>
</html>
