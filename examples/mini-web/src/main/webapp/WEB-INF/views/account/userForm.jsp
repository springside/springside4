<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<title>Mini-Web 帐号管理</title>
	
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
					checkedGroupIds:"required"
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
	<h4 class="prepend-top">管理用户</h4>
	<form:form id="inputForm" modelAttribute="user" action="${ctx}/account/user/save/${user.id}" method="post">
		<input type="hidden" name="id" value="${user.id}"/>
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
				<label for="password">密码:</label>
				<input type="password" id="password" name="password" size="40" value="${user.password}"/>
			</p>
			<p>
				<label for="passwordConfirm">确认密码:</label>
				<input type="password" id="passwordConfirm" name="passwordConfirm" size="40" value="${user.password}"/>
			</p>
			<p>
				<label for="loginName">邮箱:</label>
				<input type="text" id="email" name="email" size="40" value="${user.email}"/>
			</p>
			<p>
				<label for="loginName">权限组:</label>
				<form:checkboxes path="groupList" items="${allGroups}" itemLabel="name" itemValue="id" />
			</p>	
		</fieldset>
		<p>
			<input class="button" type="submit" value="提交"/>&nbsp;	
			<input class="button" type="button" value="返回" onclick="history.back()"/>
		</p>
	</form:form>
</body>
</html>
