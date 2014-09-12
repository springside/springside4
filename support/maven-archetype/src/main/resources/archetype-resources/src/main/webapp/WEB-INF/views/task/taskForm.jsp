#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${symbol_dollar}{pageContext.request.contextPath}"/>
<html>
<head>
	<title>任务管理</title>
</head>

<body>
	<form id="inputForm" action="${symbol_dollar}{ctx}/task/${symbol_dollar}{action}" method="post" class="form-horizontal">
		<input type="hidden" name="id" value="${symbol_dollar}{task.id}"/>
		<fieldset>
			<legend><small>管理任务</small></legend>
			<div class="control-group">
				<label for="task_title" class="control-label">任务名称:</label>
				<div class="controls">
					<input type="text" id="task_title" name="title"  value="${symbol_dollar}{task.title}" class="input-large required" minlength="3"/>
				</div>
			</div>	
			<div class="control-group">
				<label for="description" class="control-label">任务描述:</label>
				<div class="controls">
					<textarea id="description" name="description" class="input-large">${symbol_dollar}{task.description}</textarea>
				</div>
			</div>	
			<div class="form-actions">
				<input id="submit_btn" class="btn btn-primary" type="submit" value="提交"/>&nbsp;	
				<input id="cancel_btn" class="btn" type="button" value="返回" onclick="history.back()"/>
			</div>
		</fieldset>
	</form>
	<script>
		${symbol_dollar}(document).ready(function() {
			//聚焦第一个输入框
			${symbol_dollar}("${symbol_pound}task_title").focus();
			//为inputForm注册validate函数
			${symbol_dollar}("${symbol_pound}inputForm").validate();
		});
	</script>
</body>
</html>
