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
		<thead><tr><th>任务编号</th><th>任务标题</th><th>操作</th></tr></thead>
		<tbody>
		<c:forEach items="${tasks}" var="task">
			<tr>
				<td>${task.id}</td>
				<td>${task.title}</td>
				<td>
					<a href="update/${task.id}" id="update-${task.id}-btn">修改</a> <a href="delete/${task.id}">删除</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	
	<a class="btn" href="create">创建任务</a>
</body>
</html>
