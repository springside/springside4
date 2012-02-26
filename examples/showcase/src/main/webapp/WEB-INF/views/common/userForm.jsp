<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<title>综合演示用例</title>	
</head>

<body>
	<h2>综合演示用例</h2>
	<form:form id="inputForm" modelAttribute="user" action="${ctx}/common/user/save/${user.id}" method="post">
		<input type="hidden" name="id" value="${user.id}"/>
		<input type="hidden" name="workingVersion" value="${user.version}"/>
		<fieldset>
			<legend>管理用户</legend>
			<div>
				<label for="loginName" class="field">登录名:</label>
				<input type="text" id="loginName" name="loginName" size="40" value="${user.loginName}"/>
			</div>
			<div>
				<label for="name" class="field">用户名:</label>
				<input type="text" id="name" name="name" size="40" value="${user.name}"/>
			</div>
			<div>
				<label for="plainPassword" class="field">密码:</label>
				<input type="password" id="plainPassword" name="password" size="40" value="${user.plainPassword}"/>
			</div>
			<div>
				<label class="field">密码散列:</label>
				<span>${user.shaPassword}</span>
			</div>
		</fieldset>
		<div>
			<input class="button" type="submit" value="提交"/>&nbsp;	
			<input class="button" type="button" value="返回" onclick="history.back()"/>
		</div>
	</form:form>
</body>
</html>
