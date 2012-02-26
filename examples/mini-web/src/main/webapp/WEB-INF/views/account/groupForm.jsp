<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<title>Mini-Web 帐号管理</title>
</head>

<body>
	<form:form id="inputForm" modelAttribute="group" action="${ctx}/account/group/save/${user.id}" method="post">
		<input type="hidden" name="id" value="${group.id}"/>
		<fieldset class="prepend-top">
			<legend>管理权限组</legend>
			<div>
				<label for="name" class="field">名称:</label>
				<input type="text" id="name" name="name" size="40" value="${user.name}"/>
			</div>
			<div>
				<label for="permissionList" class="field">权限列表:</label>
				<form:checkboxes path="permissionList" items="${allPermissions}" itemLabel="displayName" itemValue="value" />
			</div>	
		</fieldset>
		<div>
			<input class="button" type="submit" value="提交"/>&nbsp;	
			<input class="button" type="button" value="返回" onclick="history.back()"/>
		</div>
	</form:form>
</body>
</html>
