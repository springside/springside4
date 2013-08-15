#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${symbol_dollar}{pageContext.request.contextPath}"/>

<html>
<head>
	<title>任务管理</title>
</head>

<body>
	<c:if test="${symbol_dollar}{not empty message}">
		<div id="message" class="alert alert-success"><button data-dismiss="alert" class="close">×</button>${symbol_dollar}{message}</div>
	</c:if>
	<div class="row">
		<div class="span4 offset7">
			<form class="form-search" action="${symbol_pound}">
				<label>名称：</label> <input type="text" name="search_LIKE_title" class="input-medium" value="${symbol_dollar}{param.search_LIKE_title}"> 
				<button type="submit" class="btn" id="search_btn">Search</button>
		    </form>
	    </div>
	    <tags:sort/>
	</div>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>任务</th><th>管理</th></tr></thead>
		<tbody>
		<c:forEach items="${symbol_dollar}{tasks.content}" var="task">
			<tr>
				<td><a href="${symbol_dollar}{ctx}/task/update/${symbol_dollar}{task.id}">${symbol_dollar}{task.title}</a></td>
				<td><a href="${symbol_dollar}{ctx}/task/delete/${symbol_dollar}{task.id}">删除</a></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	
	<tags:pagination page="${symbol_dollar}{tasks}" paginationSize="5"/>

	<div><a class="btn" href="${symbol_dollar}{ctx}/task/create">创建任务</a></div>
</body>
</html>
