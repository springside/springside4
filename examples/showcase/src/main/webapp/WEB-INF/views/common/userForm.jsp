<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<title>综合演示用例</title>	
	<script src="${ctx}/static/jquery-validation/1.9.0/jquery.validate.min.js" type="text/javascript"></script>
	<script src="${ctx}/static/jquery-validation/1.9.0/messages_cn.js" type="text/javascript"></script>
	<link href="${ctx}/static/jquery-validation/1.9.0/validate.css" type="text/css" rel="stylesheet" />
	
	<script>
		$(document).ready(function() {
			$("#inputForm").validate();
		});
	</script>
</head>

<body>
	<h2>综合演示用例</h2>
	<form:form id="inputForm" modelAttribute="user" action="${ctx}/common/user/save/${user.id}" method="post">
		<input type="hidden" name="id" value="${user.id}"/>
		<fieldset>
			<legend>管理用户</legend>
			<div>
				<label for="loginName" class="field">登录名:</label>
				<input type="text" id="loginName" name="loginName" size="40" value="${user.loginName}" class="required"/>
			</div>
			<div>
				<label for="name" class="field">用户名:</label>
				<input type="text" id="name" name="name" size="40" value="${user.name}" class="required"/>
			</div>
			<div>
				<label for="plainPassword" class="field">密码:</label>
				<input type="password" id="plainPassword" name="plainPassword" size="40"/>
			</div>
			<div>
				<label for="status" class="field">状态:</label>
				<form:select path="status" items="${allStatus}"/>
			</div>
		</fieldset>
		<div>
			<input id="submit" class="button" type="submit" value="提交"/>&nbsp;	
			<input id="cancel" class="button" type="button" value="返回" onclick="history.back()"/>
		</div>
	</form:form>
</body>
</html>
