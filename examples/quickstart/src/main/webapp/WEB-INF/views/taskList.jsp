<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<title>任务管理</title>
</head>

<body>
	<c:if test="${not empty message}">
		<div id="message" class="alert alert-success"><button data-dismiss="alert" class="close">×</button>${message}</div>
	</c:if>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>任务标题</th><th>管理</th></tr></thead>
		<tbody>
		<c:forEach items="${tasks}" var="task">
			<tr>
				<td><a href="${ctx}/task/update/${task.id}">${task.title}</a></td>
				<td><a href="${ctx}/task/delete/${task.id}">删除</a></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	
	<a class="btn" href="${ctx}/task/create">创建任务</a>
</body>
</html>
