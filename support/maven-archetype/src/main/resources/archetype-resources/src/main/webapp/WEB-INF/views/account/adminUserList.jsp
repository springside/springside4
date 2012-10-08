#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${symbol_dollar}{pageContext.request.contextPath}"/>

<html>
<head>
	<title>用户管理</title>
</head>

<body>
	<c:if test="${symbol_dollar}{not empty message}">
		<div id="message" class="alert alert-success"><button data-dismiss="alert" class="close">×</button>${symbol_dollar}{message}</div>
	</c:if>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>登录名</th><th>用户名</th><th>注册时间<th>管理</th></tr></thead>
		<tbody>
		<c:forEach items="${symbol_dollar}{users}" var="user">
			<tr>
				<td><a href="${symbol_dollar}{ctx}/admin/user/update/${symbol_dollar}{user.id}">${symbol_dollar}{user.loginName}</a></td>
				<td>${symbol_dollar}{user.name}</td>
				<td>
					<fmt:formatDate value="${symbol_dollar}{user.registerDate}" pattern="yyyy年MM月dd日  HH时mm分ss秒" />
				</td>
				<td><a href="${symbol_dollar}{ctx}/admin/user/delete/${symbol_dollar}{user.id}">删除</a></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
