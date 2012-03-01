<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ page import="org.apache.shiro.authc.ExcessiveAttemptsException"%>
<%@ page import="org.apache.shiro.authc.IncorrectCredentialsException"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<title>登录页</title>
	<script>
		$(document).ready(function() {
			$("#loginForm").validate();
		});
	</script>
</head>

<body>
	<%
	String error = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
	if(error != null){
	%>
		<div class="error prepend-top">登录失败，请重试.</div>
	<%
	}
	%>
	<%
	if(request.getParameter("unauthorized")!=null){
	%>
		<div class="error prepend-top">用户无权限，请登录其他用户或<a href="javascript:history.back()">返回上一页</a></div>
	<%
	}
	%>
	<form:form id="loginForm"  action="${ctx}/login" method="post">
		<fieldset class="prepend-top">
			<legend>登录</legend>
			<div>
				<label for="username" class="field">名称:</label>
				<input type="text" id="username" name="username" size="40" value="${username}" class="required"/>
			</div>
			<div>
				<label for="password" class="field">密码:</label>
				<input type="password" id="password" name="password" size="40"  class="required"/>
			</div>
		</fieldset>
		<div>
			<input type="checkbox" id="rememberMe" name="rememberMe"/> <label for="rememberMe">记住我</label>
			<span style="padding-left:10px;"><input id="submit" class="button" type="submit" value="登录"/></span>
		</div>
			<div>(管理员<b>admin/admin</b>, 普通用户<b>user/user</b>)</div>
	</form:form>
</body>
</html>
