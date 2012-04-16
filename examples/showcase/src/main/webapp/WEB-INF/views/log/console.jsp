<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<html>
<head>
<title>邮件演示</title>
</head>

<body>
	<h2>Log4j控制頁面</h2>
	
	<form:form id="defaultLoggerForm" action="${ctx}/log/defaultsetting" method="post">
		<div>
			<label for="rootLoggerLevel">Root Logger Level:</label>	<form:select path="rootLoggerLevel" items="${levels}"/> 
		</div>
		<div>
				<label for="rootLoggerLevel">Project Logger Level:</label>	<form:select path="projectLoggerLevel" items="${levels}"/>
		</div>		 
		<div>
			<input type="submit" value="save"/>
		</div>
	</form:form>
	
	<form:form id="anyLoggerForm" action="${ctx}/log/loggersetting" method="post">
		<div>
			<label for="loggerName">Logger Name:</label><input name="loggerName" value="${command.loggerName}"/> <form:select path="loggerLevel" items="${levels}"/>
		</div>
		<div>
		<c:if test="${command.loggerName ==null}">
			<input type="submit" value="query"/> 
		</c:if>
		
		<c:if test="${command.loggerName !=null}">
			<input type="submit" value="save"/>
		</c:if>
		</div>
	</form:form>
</body>
</html>