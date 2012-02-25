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
	<h2>管理用户</h2>
	<form:form id="inputForm" modelAttribute="user" action="${ctx}/common/user/save/${user.id}" method="post">
		<input type="hidden" name="id" value="${user.id}"/>
		<input type="hidden" name="workingVersion" value="${user.version}"/>
		<fieldset>
			<p>
				<label for="loginName">登录名:</label>
				<input type="text" id="loginName" name="loginName" size="40" value="${user.loginName}"/>
			</p>
			<p>
				<label for="name">用户名:</label>
				<input type="text" id="name" name="name" size="40" value="${user.name}"/>
			</p>
			<p>
				<label for="plainPassword">密码:</label>
				<input type="password" id="plainPassword" name="password" size="40" value="${user.plainPassword}"/>
			</p>
			<p>
				<label>密码散列:</label>
				<span>${user.shaPassword}</span>
			</p>
		</fieldset>
		<p>
			<input class="button" type="submit" value="提交"/>&nbsp;	
			<input class="button" type="button" value="返回" onclick="history.back()"/>
		</p>
	</form:form>
</body>
</html>
