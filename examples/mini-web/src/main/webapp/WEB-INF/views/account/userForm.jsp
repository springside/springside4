<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<title>帐号管理</title>
	
	<script>
		$(document).ready(function() {
			//聚焦第一个输入框
			$("#loginName").focus();
			//为inputForm注册validate函数
			$("#inputForm").validate({
				rules: {
					loginName: {
						required: true,
						remote: "${ctx}/account/user/checkLoginName?oldLoginName=" + encodeURIComponent('${user.loginName}')
					},
					name: "required",
					password: {
						required: true,
						minlength:3
					},
					passwordConfirm: {
						equalTo:"#password"
					},
					email:"email",
					groupList:"required"
				},
				messages: {
					loginName: {
						remote: "用户登录名已存在"
					},
					passwordConfirm: {
						equalTo: "输入与上面相同的密码"
					}
				}
			});
		});
	</script>
</head>

<body>
	<form:form id="inputForm" modelAttribute="user" action="${ctx}/account/user/save/${user.id}" method="post">
		<input type="hidden" name="id" value="${user.id}"/>
		<fieldset class="prepend-top">
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
				<label for="password" class="field">密码:</label>
				<input type="password" id="password" name="password" size="40" value="${user.password}"/>
			</div>
			<div>
				<label for="passwordConfirm" class="field">确认密码:</label>
				<input type="password" id="passwordConfirm" name="passwordConfirm" size="40" value="${user.password}"/>
			</div>
			<div>
				<label for="loginName" class="field">邮箱:</label>
				<input type="text" id="email" name="email" size="40" value="${user.email}"/>
			</div>
			<div>
				<label for="loginName" class="field">权限组:</label>
				<form:checkboxes path="groupList" items="${allGroups}" itemLabel="name" itemValue="id" />
			</div>	
		</fieldset>
		<div>
			<input id="submit" class="button" type="submit" value="提交"/>&nbsp;	
			<input id="cancel" class="button" type="button" value="返回" onclick="history.back()"/>
		</div>
	</form:form>
</body>
</html>
