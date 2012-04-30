<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<html>
<head>
	<title>日志演示</title>
	<script>
		$(document).ready(function() {
			$("#log-tab").addClass("active");
		});
	</script>
</head>

<body>
	<h2>Log4j控制頁面</h2>
	<h3>管理基本级别</h3>
	<form:form id="defaultLoggerForm" action="${ctx}/log/defaultsetting" method="post" class="form-horizontal">
		<div class="control-group">
			<label for="rootLoggerLevel" class="control-label">Root Logger Level:</label>
			<div class="controls">
				<form:select path="rootLoggerLevel" items="${levels}" class="span2"/>
			</div> 
		</div>
		<div class="control-group">
			<label for="rootLoggerLevel" class="control-label">Project Logger Level:</label>
			<div class="controls">
				<form:select path="projectLoggerLevel" items="${levels}" class="span2"/>
			</div>
		</div>		 
		<div class="form-actions">
			<input type="submit" value="save" class="btn btn-primary"/>
		</div>
	</form:form>
	<h3>管理任意Logger级别</h3>
	<form:form id="anyLoggerForm" action="${ctx}/log/loggersetting" method="post" class="form-horizontal">
		<div class="control-group">
			<label for="loggerName" class="control-label">Logger Name:</label>
			<div class="controls">
				<input type="text" name="loggerName" value="${command.loggerName}" class="span3"/>
				<form:select path="loggerLevel" items="${levels}" class="span2"/>
			</div>
		</div>
		<div class="form-actions">
		<c:if test="${command.loggerName ==null}">
			<input type="submit" value="query" class="btn btn-primary"/> 
		</c:if>
		
		<c:if test="${command.loggerName !=null}">
			<input type="submit" value="save" class="btn btn-primary"/>
		</c:if>
		</div>
	</form:form>
</body>
</html>